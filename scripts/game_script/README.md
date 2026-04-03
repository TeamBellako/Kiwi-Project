# README.md

# Script Dialogue Extractor to CSV

This script processes a folder of Markdown files containing game script content and converts the spoken dialogue into a structured CSV file.

It sends each Markdown document to a model through Replicate, asks the model to:

1. clean the script so that only dialogue remains
2. translate dialogue into English
3. return the result as CSV
4. preserve dialogue flow with `next_event_id` and `next_event`

The generated rows are then appended into a single `rows.csv` file.

---

## What it does

For every `.md` file inside `files_to_process`:

* reads the file content
* sends it to `openai/gpt-5.2` through Replicate
* expects CSV output with 7 columns
* validates and parses the CSV
* remaps IDs so they continue from the last row already stored in `rows.csv`
* appends the processed rows to the output file
* waits 10 seconds before processing the next file

---

## Output format

The resulting CSV contains these columns:

* `id`
* `character`
* `dialog`
* `next_event_id`
* `next_event`
* `name`
* `chapter`

### Column meaning

* **id**: unique dialogue row ID
* **character**: speaker name
* **dialog**: translated dialogue in English
* **next_event_id**: ID of the following dialogue line, or empty if the branch ends
* **next_event**: `CONVERSATION` if another line follows, otherwise `END`
* **name**: node name plus line number inside that node
* **chapter**: node name, prefixed according to the prompt rules

---

## Project structure

```text
.
├── files_to_process/
│   ├── 1.md
│   ├── 2.md
│   └── ...
├── rows.csv
└── script.py
```

* `files_to_process/`: input Markdown files to process
* `rows.csv`: output CSV file
* `script.py`: the processing script

---

## Requirements

* Python 3.9+
* A Replicate account
* A valid `REPLICATE_API_TOKEN`

### Python dependency

Install the required package:

```bash
pip install replicate
```

---

## Environment setup

Set your Replicate API token before running the script.

### macOS / Linux

```bash
export REPLICATE_API_TOKEN=your_token_here
```

### Windows PowerShell

```powershell
$env:REPLICATE_API_TOKEN="your_token_here"
```

---

## How to use

1. Create a folder named `files_to_process`
2. Put your `.md` script files inside it
3. Make sure they are named in a way that includes numbers, such as `1.md`, `2.md`, `10.md`
4. Run the script

```bash
python script.py
```

---

## Processing order

Files are processed in numeric order based on the number found in the filename.

Examples:

* `1.md`
* `2.md`
* `10.md`

This is handled by the `extract_number()` helper.

---

## ID handling

The script supports appending to an existing `rows.csv`.

* If `rows.csv` does not exist, it creates it and writes the header
* If `rows.csv` already exists, it reads the last ID used
* New rows returned by the model are shifted so IDs continue without collisions
* `next_event_id` values are also adjusted to match the new global IDs

---

## Prompt behavior

The model is instructed to:

* remove narration, actions, conditions, combat, and system text
* keep only character dialogue
* remove annotations like parentheses or brackets when they are not part of spoken dialogue
* preserve node names
* translate dialogue to English without inventing new text
* handle branching dialogue faithfully
* generate valid CSV output only

The prompt is written in Spanish because the source material appears to be Spanish-language game script content.

---

## Error handling

The script includes basic safeguards:

* checks whether `REPLICATE_API_TOKEN` is set
* skips execution if no Markdown files are found
* strips Markdown code fences if the model wraps the CSV in them
* removes the CSV header if the model includes it
* validates that each parsed row has exactly 7 columns
* catches per-file errors so one bad file does not stop the full batch

---

## Important note about IDs in the prompt

The prompt asks the model to start IDs at `1083`, but the script also remaps IDs when appending rows to `rows.csv`.

That means the final written IDs depend on:

* the IDs returned by the model
* the current last ID already present in `rows.csv`

If you want the written IDs to match the model output exactly, you may need to revise either:

* the prompt instruction about starting at `1083`, or
* the remapping logic in `append_csv_rows()`

---

## Example workflow

Given these files:

```text
files_to_process/
├── 1_intro.md
├── 2_town.md
└── 3_castle.md
```

Running the script will:

* process `1_intro.md`
* append its dialogue rows to `rows.csv`
* wait 10 seconds
* process `2_town.md`
* append more rows
* wait 10 seconds
* process `3_castle.md`
* append more rows

At the end, `rows.csv` contains all extracted dialogue rows in one file.

---

## Limitations

* The quality of the output depends on the model following the prompt correctly
* Branching dialogue is inferred from the structure in the source text, so complex scripts may need manual review
* The script assumes the model returns valid CSV with exactly 7 columns
* Filenames without numbers are sorted as `0`
