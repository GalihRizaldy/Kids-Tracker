package com.android.kidstracker.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.android.kidstracker.ui.theme.Shapes

// From DESIGN.md:
// Small Components (Buttons, Chips): 8px (0.5rem)
// Medium Components (Cards, Dialogs): 16px (1rem)
// Large Components (Navigation Sheets): 28px (1.75rem)
val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(28.dp)
)
