# RecipeBox

RecipeBox is a modern Android application for managing your favorite recipes and organizing them into custom collections. It is built with Jetpack Compose, Room, Hilt, and follows modern Android architecture best practices.

## Features

- **Add Recipes:** Create new recipes with details, ingredients, steps, images, and more.
- **Browse & Search:** Easily browse all recipes or search for specific ones.
- **Collections:** Organize your recipes into custom collections (e.g. "Desserts", "Vegan", etc). The default "All" collection is always available.
- **Save to Collection:** Save any recipe to one or more collections, directly from the details page.
- **Custom Collections:** Create new collections from the "Saved" screen or the recipe details bottom sheet.
- **Material 3 Design:** Clean, modern UI using Jetpack Compose and Material 3.
- **Persistence:** All data is saved locally using Room.

## Screenshots

*(Add screenshots here)*

## Architecture

- **UI:** Jetpack Compose, Material 3 components, Navigation.
- **Persistence:** Room database with DAOs for recipes and collections.
- **Dependency Injection:** Hilt.
- **Domain Layer:** Use cases and repository abstraction.
- **ViewModels:** State management using StateFlow and Compose.




## How to Build & Run

1. **Clone the repo:**
   ```sh
   git clone https://github.com/iEmadRabie/RecipeBox.git
   cd RecipeBox
   ```

2. **Open in Android Studio.**
    - Make sure you have the latest stable Android Studio and Kotlin plugin.

3. **Build the project.**
    - The app uses Gradle. All dependencies should sync automatically.

4. **Run on device or emulator.**

## Customization

- **Add your own collections:** Use the "Saved" tab (+) to create collections.
- **Save recipes to collections:** Tap the favorite (♥) icon on any recipe detail and select a collection or create a new one.
- **Manage recipes and collections:** All data is local; feel free to experiment!

## Contributing

Pull requests welcome! For major changes, please open an issue first to discuss what you’d like to change.

## License

[MIT](LICENSE)

---

**Made with ♥ by [iEmadRabie](https://github.com/iEmadRabie) and contributors.**
