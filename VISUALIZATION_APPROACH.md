# Recommended Visualization Approach

## Current Issues
1. **Limited flexibility**: Only X vs Y approach doesn't support many plot types
2. **Numeric-only**: Categorical data can't be used (e.g., bar charts with categories on X-axis)
3. **Not interactive-ready**: Current approach doesn't easily extend to drag-and-drop or interactive selection

## Recommended: Role-Based Plot Configuration

### Core Concept
Instead of "select columns → choose X-axis", use **plot type first → role-based column assignment**.

### Plot Roles (vary by plot type)

#### Common Roles:
- **X-Axis**: Can be numeric OR categorical (depending on plot type)
- **Y-Axis**: Usually numeric, can be multiple columns
- **Color/Group**: Categorical column to group/color series
- **Size**: Numeric column for bubble/scatter size
- **Facet**: Categorical for subplots (future)

### Plot Type Requirements

| Plot Type | X-Axis | Y-Axis | Color/Group | Notes |
|-----------|--------|--------|-------------|-------|
| **Scatter** | Numeric | Numeric (1+) | Categorical (opt) | Multiple Y = multiple series |
| **Line** | Numeric | Numeric (1+) | Categorical (opt) | Multiple Y = multiple lines |
| **Bar** | Categorical | Numeric (1+) | Categorical (opt) | Grouped/stacked bars |
| **Histogram** | Numeric | - | Categorical (opt) | Counts frequency |
| **Heatmap** | Categorical | Categorical | Numeric | 2D grid with color intensity |
| **Box Plot** | Categorical | Numeric | Categorical (opt) | Distribution per category |

### UI Design

```
┌─────────────────────────────────────────┐
│ Visualization Configuration             │
├─────────────────────────────────────────┤
│ Plot Type: [Scatter ▼]                  │
│                                         │
│ X-Axis:     [Column ▼] (Numeric/Cat)   │
│ Y-Axis:     [Column ▼] (Numeric) [+]    │  ← Can add multiple
│ Color By:   [None ▼] (Categorical)      │  ← Optional
│                                         │
│ [Visualize] [Reset]                     │
└─────────────────────────────────────────┘
```

### Benefits

1. **Flexible**: Supports all plot types naturally
2. **Categorical support**: Bar charts, grouped plots work correctly
3. **Multiple configurations**: Easy to switch between plot types
4. **Interactive-ready**: 
   - Can add drag-and-drop later
   - Role-based slots are perfect for visual assignment
   - Easy to add/remove Y-axis columns dynamically
5. **Clear intent**: User explicitly assigns roles
6. **Extensible**: Easy to add new roles (Size, Facet, etc.)

### Implementation Strategy

1. **Plot Type Selector** → Updates available roles
2. **Role-Based Selectors** → Show/hide based on plot type
3. **Column Type Filtering** → Each role only shows compatible columns
4. **Dynamic Y-Axis** → Allow adding/removing multiple Y columns
5. **Validation** → Check role requirements before visualizing

### Example Flows

**Bar Chart:**
- Plot Type: Bar
- X-Axis: "Occupation" (categorical)
- Y-Axis: "Net Worth" (numeric)
- Color By: "Location" (categorical) → Creates grouped bars

**Scatter with Groups:**
- Plot Type: Scatter
- X-Axis: "Age" (numeric)
- Y-Axis: "Net Worth" (numeric)
- Color By: "Occupation" (categorical) → Colors points by occupation

**Multi-line Chart:**
- Plot Type: Line
- X-Axis: "Age" (numeric)
- Y-Axis: "Net Worth", "Salary" (multiple numeric) → Two lines
- Color By: (optional) → Further grouping

### Future Interactivity

This approach naturally extends to:
- **Drag-and-drop**: Drag columns to role slots
- **Visual role indicators**: Show column types with icons
- **Live preview**: Update chart as roles change
- **Saved configurations**: Save/load plot setups
- **Template system**: Pre-configured plot types

### Migration Path

1. Keep current column selection (for highlighting)
2. Add role-based panel below plot type selector
3. Update `VisualizationInputData` to include role assignments
4. Update factories to handle categorical X-axis
5. Gradually deprecate old X-axis-only approach

