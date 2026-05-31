package com.bellako.kiwi.features.users.screens

// AGSL source for the "smoke limbo" ambient VFX on the signup screens. Held as a
// Kotlin string constant (not a raw resource) so syntax errors surface at
// hot-reload time and theme constants can be interpolated in later if wanted.
//
// Pattern: a soft, slow-drifting fog built from 4-octave FBM value noise, the
// same noise primitives used by the map water shader (WaterShaderAgsl.kt). The
// noise field is domain-warped by a second low-frequency FBM so the smoke
// churns instead of sliding rigidly, and the whole field drifts slowly upward
// over iTime. The noise is shaped into wisps with a smoothstep "density"
// window, tinted a cool pale blue-grey, biased a little denser toward the
// bottom of the screen (so it never crowds the titles up top), and kept at a
// deliberately low max opacity — it's meant to read as atmosphere, not weather.
//
// Output is premultiplied RGBA with a low alpha: the overlay sits over the
// onboarding backdrop but under the UI, so the backdrop shows through the haze
// and the text/forms stay crisp. The `content` uniform is required by
// RenderEffect.createRuntimeShaderEffect's second argument; it is not sampled.

internal const val SMOKE_LIMBO_SHADER_SRC = """
uniform float2 iResolution;
uniform float iTime;

// Layer content input — required by RenderEffect.createRuntimeShaderEffect's
// second argument. Not sampled; the shader paints its own smoke.
uniform shader content;

// ---- Tunables ---------------------------------------------------------------
// Noise wavelength in layer-local pixels. Larger = bigger, softer, slower-
// reading wisps; smaller = busier, more granular smoke. Big values read as
// large, billowing dreamlike clouds.
const float NOISE_SCALE = 640.0;

// Upward drift of the whole field, in pixels per second. Set to 0 so the smoke
// stays anchored in place and only wiggles (the churn comes from the domain warp
// below). Give this a small positive value if you ever want a slow directional
// rise again.
const float DRIFT_SPEED = 0.0;

// Roam: a slow looping offset that wanders the whole field around the screen on
// a sin/cos (Lissajous) path. Because it oscillates it never travels off in one
// direction — the smoke just drifts around the canvas. RADIUS is how far it
// roams in px; SPEED is how fast it loops.
const float ROAM_RADIUS = 260.0;
const float ROAM_SPEED = 0.09;

// Domain warp: a low-frequency FBM nudges the sample point so the smoke folds
// and curls. AMOUNT is the max displacement in px (the main "how much does it
// wiggle" knob); SCALE is its wavelength; SPEED is how fast the warp evolves.
const float WARP_AMOUNT = 280.0;
const float WARP_SCALE = 760.0;
const float WARP_SPEED = 0.18;

// Density window: the FBM (0..1) is remapped through this smoothstep so only the
// upper range becomes visible smoke, leaving clear gaps between wisps. Raise
// FLOOR for thinner/sparser smoke; lower it for more coverage.
const float DENSITY_FLOOR = 0.40;
const float DENSITY_CEIL = 0.98;

// Overall haze strength. This is the single "how subtle" knob — at 0.18 the
// smoke is a faint veil. Push toward ~0.4 to make the plumbing obvious while
// testing, then bring it back down.
// DEBUG: bumped from 0.18 to 0.55 to make the smoke obvious while confirming it
// renders. Bring back to ~0.18 once the plumbing is verified.
const float MAX_OPACITY = 0.55;

// Cool pale blue-grey. Sits naturally over a dark/teal onboarding backdrop.
const half3 SMOKE_COLOR = half3(0.78, 0.85, 0.95);

// Vertical bias: smoke is at TOP_STRENGTH near the top of the screen and full
// strength toward the bottom, so headings stay clear and the haze settles low.
const float TOP_STRENGTH = 0.45;
// -----------------------------------------------------------------------------

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

float fbm(float2 p) {
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

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution;

    // Slow upward drift of the sampling point (subtract so the field appears to
    // rise as time increases). DRIFT_SPEED is 0 by default — the motion comes
    // from the roam + warp below.
    float2 base = fragCoord - float2(0.0, iTime * DRIFT_SPEED);

    // Roam: wander the whole field around the screen on a looping sin/cos path.
    // Different x/y frequencies make the loop a slow Lissajous wander rather than
    // a rigid circle, so the smoke feels like it's floating around the canvas.
    float2 roam = float2(
        sin(iTime * ROAM_SPEED),
        cos(iTime * ROAM_SPEED * 0.73)
    ) * ROAM_RADIUS;
    base += roam;

    // Low-frequency domain warp so the smoke curls instead of sliding rigidly.
    float2 warpUv = base / WARP_SCALE + iTime * WARP_SPEED;
    float2 warpVec =
        float2(fbm(warpUv), fbm(warpUv + float2(19.7, 7.3))) - 0.5;
    float2 warped = base + warpVec * WARP_AMOUNT;

    // Wisps: shape the noise into soft bands of smoke with clear gaps.
    float n = fbm(warped / NOISE_SCALE);
    float density = smoothstep(DENSITY_FLOOR, DENSITY_CEIL, n);

    // Keep the top of the screen clearer than the bottom.
    float vBias = mix(TOP_STRENGTH, 1.0, clamp(uv.y, 0.0, 1.0));

    float alpha = density * MAX_OPACITY * vBias;

    // Premultiplied output: rgb scaled by alpha, alpha in the w channel.
    half a = half(alpha);
    return half4(SMOKE_COLOR * a, a);
}
"""
