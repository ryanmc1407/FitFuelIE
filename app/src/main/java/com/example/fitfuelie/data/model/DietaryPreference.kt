package com.example.fitfuelie.data.model

/**
 * DietaryPreference Enum
 * 
 * Represents the user's dietary restrictions or preferences.
 * Used to filter meal suggestions and ensure recommendations match their diet.
 * Set during onboarding and can be updated in the profile.
 */
enum class DietaryPreference {
    VEGETARIAN,      // No meat, but may include eggs/dairy
    VEGAN,          // No animal products at all
    GLUTEN_FREE,    // No gluten-containing foods
    KETO,           // Low-carb, high-fat diet
    NO_RESTRICTIONS // No dietary restrictions
}
