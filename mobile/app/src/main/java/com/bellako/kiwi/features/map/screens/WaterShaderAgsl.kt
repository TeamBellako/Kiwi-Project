package com.bellako.kiwi.features.map.screens

// AGSL source for the animated water VFX on the map. Held as a Kotlin string
// constant (not a raw resource) so syntax errors surface at hot-reload time and
// constants from the rest of the codebase can be interpolated in later phases.
//
// Pattern: contour-band shading over a 4-octave FBM noise field. Foam lines
// are smooth bands sampled at multiple noise isolines — naturally wiggly,
// varying in thickness, with the occasional pinched-off white dot.
//
// The noise scale is modulated per-fragment by a "width score": for each
// fragment we sample the mask at eight points around a ring and count how
// many landed in water. Narrow rivers land few hits → blend toward a fine,
// dense noise scale. Open seas land all hits → blend toward a coarse, sparser
// noise scale. The result is that foam shapes shrink in tight channels and
// grow in open water, suggesting flow rate and surface scale at once.
//
// Everything is domain-warped by a second, lower-frequency FBM and flow-
// translated from an upper-middle source toward the bottom (mountains →
// rivers feel). Mask gates the effect to water-only regions; the shader
// inherits pan/zoom by sitting inside the same transformed Box as the map.

internal const val WATER_SHADER_SRC = """
uniform float2 iResolution;
uniform float2 maskResolution;
uniform float iTime;

// Layer content input — required by RenderEffect.createRuntimeShaderEffect's
// second argument. Not sampled.
uniform shader content;

// Water mask, white = water, black = land.
uniform shader mask;

// Noise wavelength endpoints, in layer-local pixels. The actual scale used at
// each fragment is blended between these by the width score. Smaller =
// denser foam web; larger = bigger blobs between foam lines.
const float NOISE_SCALE_NARROW = 35.0;
const float NOISE_SCALE_WIDE = 110.0;

// Width probe: ring radius in layer-local pixels, and how many directions to
// sample. A larger radius differentiates wider channels but smooths out fine
// structure. 8 samples on an octagon is a decent balance.
const float WIDTH_PROBE_RADIUS = 55.0;
const int WIDTH_SAMPLES = 8;

// Domain warp.
const float WARP_SCALE = 280.0;
const float WARP_AMOUNT = 95.0;

// Flow.
const float FLOW_SPEED = 20.0;
const float2 FLOW_SOURCE = float2(0.50, 0.15);
const float RADIAL_BLEND = 0.55;

// Foam isolines. Three different noise levels, each with its own band width.
// Multiple isolines give the foam network varied thickness and produces the
// small isolated white dots seen in the reference.
const float FOAM_LEVEL_A = 0.50;
const float FOAM_WIDTH_A = 0.040;
const float FOAM_LEVEL_B = 0.32;
const float FOAM_WIDTH_B = 0.022;
const float FOAM_LEVEL_C = 0.66;
const float FOAM_WIDTH_C = 0.022;

// Dark-blue patch threshold.
const float DARK_PATCH_MAX = 0.30;
const float DARK_PATCH_MIN = 0.18;

// Palette anchored on the Kiwi theme's colorOcean = #007DAD.
const half3 DEEP_BLUE = half3(0.050, 0.450, 0.620);
const half3 SHALLOW_BLUE = half3(0.130, 0.570, 0.750);
const half3 FOAM_COLOR = half3(0.950, 0.985, 1.000);

// Overall dampening so the JPEG's water color blends through.
const float FOAM_OPACITY = 0.75;
const float EFFECT_OPACITY = 0.55;

float hash21(float2 p) {
    return fract(sin(dot(p, float2(127.1, 311.7))) * 43758.5453);
}

float valueNoise(float2 p) {
    float2 n = floor(p);
    float2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash21(n);
    float b = hash21(n + float2(1.0, 0.0));
    float c = hash21(n + float2(0.0, 1.0));
    float d = hash21(n + float2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm4(float2 p) {
    float v = 0.0;
    float a = 0.5;
    float total = 0.0;
    for (int i = 0; i < 4; i++) {
        v += a * valueNoise(p);
        total += a;
        p = p * 2.03;
        a *= 0.5;
    }
    return v / total;
}

float fbm2(float2 p) {
    return 0.5 * valueNoise(p) + 0.25 * valueNoise(p * 2.03);
}

float band(float v, float center, float halfWidth) {
    return 1.0 - smoothstep(0.0, halfWidth, abs(v - center));
}

// Returns 0..1: fraction of probe directions whose neighborhood pixel is
// water. Used to differentiate narrow channels (low score) from open seas
// (high score) so we can pick a per-fragment noise scale.
float widthScore(float2 fragCoord) {
    float count = 0.0;
    for (int i = 0; i < WIDTH_SAMPLES; i++) {
        float angle = float(i) * (6.2831853 / float(WIDTH_SAMPLES));
        float2 dir = float2(cos(angle), sin(angle));
        float2 probeFrag = fragCoord + dir * WIDTH_PROBE_RADIUS;
        float2 probeMaskCoord = (probeFrag / iResolution) * maskResolution;
        count += step(0.5, mask.eval(probeMaskCoord).r);
    }
    return count / float(WIDTH_SAMPLES);
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;
    float2 maskCoord = uv * maskResolution;
    half waterness = mask.eval(maskCoord).r;

    if (waterness < 0.01) {
        return half4(0.0);
    }

    // Flow direction at this fragment.
    float2 sourcePix = FLOW_SOURCE * iResolution;
    float2 toFrag = fragCoord - sourcePix;
    float toFragLen = length(toFrag);
    float2 radialDir = (toFragLen > 0.0001) ? toFrag / toFragLen : float2(0.0, 1.0);
    float2 flowDir = normalize(mix(float2(0.0, 1.0), radialDir, RADIAL_BLEND));
    float2 scrollOffset = flowDir * iTime * FLOW_SPEED;

    float2 base = fragCoord - scrollOffset;

    // Domain warp.
    float2 warpUv = base / WARP_SCALE + iTime * 0.03;
    float2 warpVec = float2(
        fbm2(warpUv),
        fbm2(warpUv + float2(31.0, 17.3))
    ) - 0.5;
    float2 warped = base + warpVec * WARP_AMOUNT;

    // Width score from probing the mask. Sample two noise fields at fixed
    // scales and blend by score: each fragment sees its own effective scale
    // without the discontinuities that come from feeding a varying scale into
    // a single noise lookup.
    float width = widthScore(fragCoord);
    float nNarrow = fbm4(warped / NOISE_SCALE_NARROW);
    float nWide = fbm4(warped / NOISE_SCALE_WIDE);
    float n = mix(nNarrow, nWide, width);

    // Dark patches in noise dips.
    half darkPatch = half(smoothstep(DARK_PATCH_MAX, DARK_PATCH_MIN, n));
    half3 baseWater = mix(SHALLOW_BLUE, DEEP_BLUE, darkPatch);

    // Foam from three isolines combined.
    float foamA = band(n, FOAM_LEVEL_A, FOAM_WIDTH_A);
    float foamB = band(n, FOAM_LEVEL_B, FOAM_WIDTH_B);
    float foamC = band(n, FOAM_LEVEL_C, FOAM_WIDTH_C);
    half foam = half(clamp(foamA + foamB * 0.7 + foamC * 0.7, 0.0, 1.0)) * half(FOAM_OPACITY);

    half3 waterRgb = mix(baseWater, FOAM_COLOR, foam);

    half alpha = waterness * half(EFFECT_OPACITY);
    return half4(waterRgb * alpha, alpha);
}
"""
