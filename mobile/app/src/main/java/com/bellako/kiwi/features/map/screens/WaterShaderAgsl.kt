package com.bellako.kiwi.features.map.screens

// AGSL source for the animated water VFX on the map. Held as a Kotlin string
// constant (not a raw resource) so syntax errors surface at hot-reload time and
// constants from the rest of the codebase can be interpolated in later phases.
//
// Phase 3: two-octave Voronoi cellular noise with foam derived from the F2-F1
// border distance trick (Inigo Quilez). Cell seeds are animated by iTime so
// the pattern slowly drifts. The result is composited only where the water
// mask says water — land regions stay fully transparent so the underlying map
// shows through unchanged.
//
// Coordinates: the shader runs in layer-local pixel space, which equals
// map-space here because the overlay sits inside the same transformed Box as
// the map image. Voronoi sampled in fragCoord stays anchored to water bodies
// under pan/zoom — no extra transform uniforms required.

internal const val WATER_SHADER_SRC = """
uniform float2 iResolution;
uniform float2 maskResolution;
uniform float iTime;

// Layer content input — required by RenderEffect.createRuntimeShaderEffect's
// second argument. Not sampled: we composite via alpha and let Compose blend
// our output over the sibling Kiwi_Image below us.
uniform shader content;

// Water mask, white = water, black = land. Sampled per fragment to gate the
// effect. BitmapShader takes pixel coords, so we scale the normalized uv by
// the mask's pixel dimensions (passed in maskResolution).
uniform shader mask;

// Cell sizes are in layer-local pixels. With the layer roughly the displayed
// map size (~1000 px wide on phone), 80 px ≈ a dozen cells across the map.
const float CELL_SIZE_A = 80.0;
const float CELL_SIZE_B = 40.0;

// Foam thickness as a fraction of cell-distance. Larger = thicker foam ring.
const float FOAM_WIDTH_A = 0.10;
const float FOAM_WIDTH_B = 0.07;

// Water palette anchored on the Kiwi theme's colorOcean = #007DAD. Deep cells
// sit a few steps darker (#005A85), shallow cells a few steps lighter and
// more saturated (#2CA8D6), foam stays pure white for maximum contrast.
const half3 DEEP_BLUE = half3(0.000, 0.353, 0.522);     // #005A85
const half3 SHALLOW_BLUE = half3(0.173, 0.659, 0.839);  // #2CA8D6
const half3 FOAM_COLOR = half3(1.000, 1.000, 1.000);    // #FFFFFF

// Standard 2D hash → 2D point in [0, 1)^2. Used to seed each cell.
float2 hash22(float2 p) {
    p = float2(
        dot(p, float2(127.1, 311.7)),
        dot(p, float2(269.5, 183.3))
    );
    return fract(sin(p) * 43758.5453);
}

// Same hash collapsed to a single float — used to pick a per-cell color mix.
float hash21(float2 p) {
    return fract(sin(dot(p, float2(127.1, 311.7))) * 43758.5453);
}

// Returns (F1, F2, cellHash). F1 is the distance to the nearest cell center,
// F2 the second nearest — F2 − F1 collapses to 0 right on a cell border, which
// is what we use to draw foam. cellHash identifies the closest cell so we can
// tint each cell differently.
float3 voronoiCell(float2 x, float timePhase) {
    float2 n = floor(x);
    float2 f = fract(x);
    float f1 = 9.0;
    float f2 = 9.0;
    float2 closestCell = float2(0.0);
    for (int j = -1; j <= 1; j++) {
        for (int i = -1; i <= 1; i++) {
            float2 g = float2(float(i), float(j));
            float2 o = hash22(n + g);
            // Animate the cell seed inside its tile so cells "breathe".
            o = 0.5 + 0.5 * sin(timePhase + 6.2831853 * o);
            float2 r = g + o - f;
            float d = dot(r, r);
            if (d < f1) {
                f2 = f1;
                f1 = d;
                closestCell = n + g;
            } else if (d < f2) {
                f2 = d;
            }
        }
    }
    return float3(sqrt(f1), sqrt(f2), hash21(closestCell));
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    float2 maskCoord = uv * maskResolution;
    half waterness = mask.eval(maskCoord).r;

    // Land — fully transparent so the map shows through unchanged.
    if (waterness < 0.01) {
        return half4(0.0);
    }

    // Slow drift. Different multipliers per octave keep the two layers from
    // beating against each other.
    float t = iTime * 0.30;

    float3 v1 = voronoiCell(fragCoord / CELL_SIZE_A, t);
    float3 v2 = voronoiCell(fragCoord / CELL_SIZE_B + 17.0, t * 1.4);

    // Cell tint from the large-octave hash so adjacent cells differ visibly.
    half cellMix = half(v1.z);
    half3 baseWater = mix(DEEP_BLUE, SHALLOW_BLUE, cellMix);

    // Foam ring at cell borders, from both octaves combined.
    float foamA = smoothstep(FOAM_WIDTH_A, 0.0, v1.y - v1.x);
    float foamB = smoothstep(FOAM_WIDTH_B, 0.0, v2.y - v2.x);
    half foam = half(clamp(foamA + foamB * 0.6, 0.0, 1.0));

    half3 waterRgb = mix(baseWater, FOAM_COLOR, foam);

    // Premultiplied alpha output. waterness fades the effect out across
    // shorelines (assumes the mask has soft edges from the Gaussian blur in
    // Photoshop). On land waterness is 0 → output is transparent.
    return half4(waterRgb * waterness, waterness);
}
"""
