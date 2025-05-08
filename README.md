# GroceryWise - CS501 Final Project

GroceryWise is a user-friendly Android app built to help users manage their grocery planning, pantry inventory, and recipe discovery—all in one place. Designed with simplicity and utility in mind, the app integrates barcode scanning, real-time price lookups, and ingredient-aware recipe suggestions to promote smarter grocery shopping and reduced food waste.

## Features

### Grocery List
- Add grocery items manually or via barcode scan.
- View estimated prices fetched from external UPC lookup API.
- Check off purchased items to automatically move them to the pantry.

### Pantry
- Keep track of available grocery items and quantities.
- Auto-prompt users to re-add depleted pantry items to the grocery list, fetching updated prices if UPC is available.
- Move items between pantry and grocery list seamlessly.

### Barcode Scanning & UPC Lookup
- Use Google ML Kit’s barcode scanning for fast UPC entry.
- Automatically fetch item name, image, and estimated price.
- Manual UPC input also supported with autofill functionality.

### Recipe Finder
- Recipe suggestions based on ingredients currently in the pantry.
- Color-coded ingredient display:
  - Green: Already in pantry
  - Red: Still needed
- Users can also search for custom recipes via Spoonacular API.

### Cloud-Backed Persistence
- All data (pantry, grocery list, images) is stored in Firebase Realtime Database and Firebase Cloud Storage.
- Secure authentication via FirebaseAuth (email/password).
- Supports login from any device with persistent user data.

### Responsive UI
- Built with Jetpack Compose and Material Design for modern, clean interfaces.
- Multi-device support:
  - Phones: Single-pane tab layout
  - Tablets: Combined vertical or side-by-side layouts for pantry and grocery list
- Adaptive routing based on screen width and orientation.

## Architecture & Tech Stack

- **Frontend:** Kotlin + Jetpack Compose
- **Navigation:** Single NavHost with nested bottom-tab NavController
- **Authentication:** FirebaseAuth (email/password with password reset support)
- **Database:** Firebase Realtime Database (per-user scoped)
- **Storage:** Firebase Cloud Storage for user-uploaded images
- **Image Uploads:** Public URLs saved in the database for reuse
- **APIs Used:**
  - [UPCItemDB](https://api.upcitemdb.com/prod/trial/lookup) for product data
  - [Spoonacular](https://api.spoonacular.com) for recipe discovery
  - [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning) for UPC scanning
- **Animation:** Lottie animation for visual polish
