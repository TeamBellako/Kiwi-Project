import csv
import time
import os
import re
from pathlib import Path

import replicate

INPUT_FOLDER = Path("files_to_process")
OUTPUT_CSV = Path("rows.csv")

CSV_HEADER = ["id", "character", "dialog", "next_event_id", "next_event", "name", "chapter"]

PROMPT = """
Quiero que me ayudes a procesar un documento de un guion de juego y convertirlo en un **CSV estructurado de diálogos**.

El documento está dividido en **capítulos** y dentro de cada capítulo hay **nodos**. Cada nodo puede contener narrativa, descripciones, acciones, instrucciones, condiciones, combates y conversaciones entre personajes.

Tu tarea consiste en hacer **dos pasos de forma consecutiva**, pero devolviendo directamente **solo el resultado final en formato CSV**.

### Paso 1: Limpieza del guion

Debes limpiar el documento siguiendo estas reglas:

1. **Elimina todo lo que no sean conversaciones**, manteniendo únicamente los nombres de los personajes y sus líneas de diálogo.
2. **Si un personaje tiene expresiones entre paréntesis junto a su nombre**, elimínalas.
    - Ejemplo: `LIRIA (CONT’D)` → `LIRIA`
    - Ejemplo: `POSADERO (gritando)` → `POSADERO`
3. **Elimina también las acotaciones o indicaciones entre paréntesis o corchetes** cuando no formen parte real del diálogo.
    - Ejemplo: `(gritando)`, `[solo si se ha elegido tal opción]`, etc. deben eliminarse.
4. **Mantén el nombre del nodo**, porque será necesario para el CSV final.
5. **Elimina toda la narrativa**, descripciones de escenas, acciones, transiciones, explicaciones del sistema, combates, eventos de mapa, instrucciones al jugador y cualquier otro texto que no sea diálogo hablado por un personaje.
6. El resultado limpio debe conservar únicamente pares del tipo:
    - `PERSONAJE: línea de diálogo`

### Paso 2: Generación del CSV

Una vez limpio el contenido, conviértelo en un CSV con estas columnas exactas:

- **id**: identificador numérico autoincremental de cada línea de diálogo, siguiendo el orden de aparición en el guion.
- **character**: nombre del personaje que dice la frase.
- **dialog**: texto exacto de la frase, ya limpio. Tienes que traducirlo al ingles, quiero que mantengas el estilo y el tono lo maximo posible. Evita generar texto nuevo y ciñete unica y exclusivamente a la traducción
- **next_event_id**: id de la siguiente línea de diálogo. Debe estar vacío si esa línea es la última de una rama o conversación.
- **next_event**: debe ser `CONVERSATION` si existe una siguiente línea de diálogo, y `END` si no existe.
- **name**: nombre del nodo seguido del número de línea dentro de ese nodo.
    - Ejemplo: `EXT. PUERTAS DE LA CIUDAD - ATARDECER 1`
- **chapter**: nombre completo del nodo
    - Ejemplo: `EXT. PUERTAS DE LA CIUDAD - ATARDECER`

### Reglas importantes para el flujo conversacional

1. **Debes incluir todas las líneas de diálogo del documento**, no solo una muestra.
2. **Respeta el orden real de aparición** de las líneas dentro de cada nodo.
3. **Gestiona correctamente las líneas de JUGADOR** cuando aparezcan como opciones o preguntas.
    - Deben incluirse como filas normales del CSV.
    - Deben conectarse correctamente mediante `next_event_id` con la respuesta que viene después en el guion.
4. Si hay varias opciones de `JUGADOR`, **cada una debe apuntar a la línea de respuesta que le corresponda** según el flujo del texto.
5. Si una línea es la última intervención de una conversación o de una rama, entonces:
    - `next_event_id` = vacío
    - `next_event` = `END`
6. Si el flujo continúa, entonces:
    - `next_event_id` = id de la siguiente línea
    - `next_event` = `CONVERSATION`
7. El campo `name` debe reiniciar la numeración dentro de cada nodo.
    - Ejemplo: si un nodo tiene 12 líneas, sus nombres van de `NOMBRE_DEL_NODO 1` a `NOMBRE_DEL_NODO 12`
    - En el siguiente nodo vuelve a empezar desde 1
8. El campo `chapter` **no es el capítulo general del documento**, sino **el nombre del nodo**. Añadele un prefijo que sea 2X, donde mantenemos el 2 y donde X es el numero del capitulo dentro del documento en general
9. Si una línea contiene texto con comas, comillas o caracteres especiales, **escápalos correctamente para CSV válido**.
10. No inventes líneas ni completes contenido que no exista en el documento. Solo estructura lo que realmente aparece.
11. Si te encuentras con la palabra NOMBRE en mayusculas, añadele un arroba antes, ademas de traducirlo. Es decir que NOMBRE pasaria a ser @NAME
12. La primera id debe ser 1083, ya que la base de datos actual ya tiene filas. A partir de ahí, cada línea nueva debe incrementar el id en 1 respecto a la anterior, sin saltos ni duplicados.

### Formato de salida

Quiero que devuelvas:
1. **Directamente el CSV final completo como texto plano**
2. **No devuelvas enlaces, no uses Markdown, no uses bloques de código**
3. **La salida debe empezar directamente por la cabecera CSV**

### Criterios de calidad

- Procesa **todo el documento completo**, no solo una parte.
- No resumas.
- No expliques el proceso salvo que haya una ambigüedad importante.
- Prioriza la **consistencia estructural** del CSV.
- Asegúrate de que los `next_event_id` tengan sentido y no queden rotos.
- Si detectas ramas de diálogo, refléjalas de la forma más fiel posible al orden y estructura del guion.
- El resultado debe ser **limpio, utilizable y listo para importar en un sistema de diálogos**.
""".strip()


def strip_code_fences(text: str) -> str:
    text = text.strip()
    if text.startswith("```"):
        text = re.sub(r"^```[a-zA-Z0-9_-]*\n?", "", text)
        text = re.sub(r"\n?```$", "", text)
    return text.strip()


def extract_csv_rows(csv_text: str):
    """
    Parse CSV returned by the model safely.
    Returns a list of rows (without header).
    """
    csv_text = strip_code_fences(csv_text)
    lines = [line for line in csv_text.splitlines() if line.strip()]
    if not lines:
        return []

    # Remove header if present
    first_line = lines[0].strip().replace(" ", "")
    expected_header = ",".join(CSV_HEADER).replace(" ", "")
    if first_line.lower() == expected_header.lower():
        lines = lines[1:]

    if not lines:
        return []

    parsed_rows = []
    reader = csv.reader(lines)
    for row in reader:
        if not row:
            continue
        if len(row) != 7:
            raise ValueError(f"Expected 7 columns, got {len(row)}: {row}")
        parsed_rows.append(row)

    return parsed_rows


def run_model(markdown_content: str) -> str:
    full_prompt = f"{PROMPT}\n\n--- DOCUMENTO ---\n\n{markdown_content}"

    input_data = {
        "prompt": full_prompt,
        "reasoning_effort": "medium",
    }

    output = []

    # Replicate Python docs show using replicate.run(...) and iterating the result
    for event in replicate.run("openai/gpt-5.2", input=input_data):
        output.append(str(event))

    return "".join(output)


def get_last_id() -> int:
    if not OUTPUT_CSV.exists() or OUTPUT_CSV.stat().st_size == 0:
        return 0

    with open(OUTPUT_CSV, "r", encoding="utf-8", newline="") as f:
        reader = csv.reader(f)
        rows = list(reader)

    if len(rows) <= 1:
        return 0

    try:
        return int(rows[-1][0])
    except Exception:
        return 0


def append_csv_rows(rows):
    file_exists = OUTPUT_CSV.exists() and OUTPUT_CSV.stat().st_size > 0
    last_id = get_last_id()

    adjusted_rows = []
    for row in rows:
        # row = [id, character, dialog, next_event_id, next_event, name, chapter]
        old_id = row[0].strip()
        old_next = row[3].strip()

        new_id = str(last_id + int(old_id))

        if old_next:
            new_next = str(last_id + int(old_next))
        else:
            new_next = ""

        adjusted_rows.append([
            new_id,
            row[1],
            row[2],
            new_next,
            row[4],
            row[5],
            row[6],
        ])

    mode = "a" if file_exists else "w"
    with open(OUTPUT_CSV, mode, encoding="utf-8", newline="") as f:
        writer = csv.writer(f)

        if not file_exists:
            writer.writerow(CSV_HEADER)

        writer.writerows(adjusted_rows)

    return len(adjusted_rows)

def extract_number(path):
    m = re.search(r"\d+", path.stem)
    return int(m.group()) if m else 0

def main():
    if not os.getenv("REPLICATE_API_TOKEN"):
        raise EnvironmentError("REPLICATE_API_TOKEN is not set.")

    md_files = sorted(INPUT_FOLDER.glob("*.md"), key=extract_number)

    if not md_files:
        print("No markdown files found.")
        return

    total_rows = 0

    for i, file in enumerate(md_files):
        print(f"Processing {file.name}...")

        try:
            content = file.read_text(encoding="utf-8")
            csv_output = run_model(content)
            rows = extract_csv_rows(csv_output)
            written = append_csv_rows(rows)

            total_rows += written
            print(f"Added {written} rows")

        except Exception as e:
            print(f"Error processing {file.name}: {e}")

        # wait 10 seconds before the next file
        if i < len(md_files) - 1:
            print("Waiting 10 seconds before next file...")
            time.sleep(10)

    print(f"\nFinished. {total_rows} rows written to {OUTPUT_CSV}")


if __name__ == "__main__":
    main()