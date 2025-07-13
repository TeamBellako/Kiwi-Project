# Feature Plan: Interactive Draggable & Zoomable Map for Home Screen (Android - Jetpack Compose)

## 📱 Goal
Allow users to **zoom**, **drag**, and **navigate** an image map on the home screen while enforcing **boundary limits** to prevent out-of-bounds scrolling.

---

## ✅ Preconditions
- Jetpack Compose set up in the project.
- Map image asset available.
- Navigation and state management handled via standard architecture (MVVM with State Hoisting preferred).
- App uses Kotlin and Compose libraries.

---

## 📐 Feature Architecture Overview
- **Composable Layer**:
    - Create a `ZoomableMap` composable.
    - Apply gesture handling for drag & zoom using `Modifier.pointerInput`.
- **ViewModel Layer**:
    - Store zoom, offset, and boundary state.
- **UI Constraints**:
    - Ensure zoom range (e.g. min 1x to max 4x).
    - Prevent panning out of bounds using calculated constraints based on current zoom.

---

## 🧩 Step-by-Step Task Breakdown

### 🧱 Task 1: Set Up Data Model & ViewModel
- [ ] Create `MapUiState`:
    - `scale: Float`
    - `offset: Offset`
    - `mapWidthPx: Float`
    - `mapHeightPx: Float`
    - `viewportWidthPx: Float`
    - `viewportHeightPx: Float`
- [ ] Create `MapViewModel`:
    - Maintain `MapUiState`.
    - Add logic to update state on zoom/drag with boundary enforcement.

---

### 🧱 Task 2: Build `ZoomableMap` Composable Skeleton
- [ ] Create `ZoomableMap(viewModel: MapViewModel)` composable.
- [ ] Load map image using `Image(painter = painterResource(...))`.
- [ ] Apply `Modifier.graphicsLayer` for scale and translation from state.
- [ ] Forward size measurements of image and screen to ViewModel.

---

### 🧱 Task 3: Handle Gesture Detection
- [ ] Use `Modifier.pointerInput(Unit) { detectTransformGestures(...) }`
- [ ] Update state:
    - Apply zoom multiplier to `scale`
    - Adjust `offset` using pan delta
- [ ] Use `consumeAllChanges()` to avoid gesture conflicts

---

### 🧱 Task 4: Enforce Zoom Limits
- [ ] In ViewModel:
    - Clamp `scale` between min (e.g. 1f) and max (e.g. 4f)

---

### 🧱 Task 5: Enforce Drag Boundaries
- [ ] Compute max/min offsets based on:
    - `(scaledMapSize - viewportSize) / 2`
- [ ] Clamp offset accordingly on drag
- [ ] Handle edge case: when map is smaller than screen

---

### 🧱 Task 6: Test Responsiveness and Multi-Touch Support
- [ ] Verify multi-touch pinch-to-zoom works on different devices
- [ ] Add test cases (Espresso/Compose Testing):
    - Zoom in/out
    - Drag within bounds
    - Attempt drag out of bounds

---

### 🧱 Task 7: UI Polishing
- [ ] Add optional over-scroll bounce or resistance effect
- [ ] Smooth zoom animation (optional using `animateFloatAsState`)
- [ ] Provide visual feedback (e.g. boundaries, zoom indicator)

---

### 🧱 Task 8: Final Integration & QA
- [ ] Integrate `ZoomableMap` into home screen layout
- [ ] Test lifecycle and recomposition handling
- [ ] Perform UX QA with different map sizes and screen dimensions

---

## 🧪 TDD & CI/CD Notes
- Unit test `MapViewModel` logic for scaling and boundary clamping.
- Integration test `ZoomableMap` gestures.
- Add feature-specific Espresso UI tests.
- Add coverage reports to CI/CD (GitHub Actions).
