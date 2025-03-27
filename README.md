
# CS501 FINAL PROJECT

Project Proposal: Smart Grocery Tracker
1. Introduction & Motivation
Managing groceries can be tedious—manually noting items, recalling prices, and ensuring you don’t overspend can become overwhelming. Smart Grocery Tracker aims to streamline this process by providing an easy‐to‐use Android application that leverages barcode scanning, real‐time price data, and recipe suggestions enhanced with detailed nutritional information. The ultimate goal is to reduce food waste, improve budgeting accuracy, and help users make the most of what they already have at home.
2. Problem Statement
Loss of Receipts & Price Tracking: Shoppers often forget item prices and lose receipts, making budgeting difficult.
Overbuying & Overspending: Without an overview of current pantry items, users purchase unneeded groceries.
Price Fluctuations: Grocery prices can change frequently, making cost estimates and budgeting unpredictable.
Missed Cooking Opportunities: Users don’t always realize they have enough ingredients for recipes they want to try.
3. Proposed Solution & Key Features
A. Grocery Inventory & Barcode Scanning
Barcode Integration: Use the device’s camera with Google ML Kit to scan item barcodes and retrieve the product code.
Pantry Management: Maintain a pantry inventory with real‐time updates and allow manual input for items without barcodes.
B. Price Estimation, Budgeting & Price Fluctuation Alerts
Real-Time Pricing: Fetch current grocery price data from an external API (via Retrofit & Moshi) and display an estimated total cost when building a shopping list.
Historical Price Trends: Track historical prices to visualize price trends (increases or decreases) over time.
Alerts & Notifications: Implement push notifications or in-app alerts to notify users when frequently purchased items experience significant price fluctuations—helping them adjust their budgeting or shopping plans.


C. Recipe Recommendations with Enhanced Nutritional Information
Smart Recipe Suggestions: Integrate the Edamam Recipe Search API to suggest recipes based on available pantry items.
Nutritional Details: Enhance recipe recommendations by including nutritional data (calories, protein, etc.), enabling users to make healthier choices and better control their dietary intake.
Food Waste Reduction: Encourage users to cook with ingredients they already have, reducing waste.
D. Data Persistence with MongoDB Atlas
Remote Database Storage: Store all user data (inventory items, shopping lists, price history, nutritional data, and usage patterns) in MongoDB Atlas.
CRUD Operations: Support full Create, Read, Update, Delete operations to fulfill the data‐persistence requirement.
(Optional: Implement a lightweight local cache using Room or DataStore for offline access.)
E. Multi-Device Responsiveness
Modern UI: Build the app in Kotlin using Jetpack Compose, following Material Design guidelines for a responsive and visually appealing UI.
Device Adaptability: Test on multiple device types (e.g., an emulated Pixel phone and a tablet) in both portrait and landscape orientations to ensure optimal user experience.
4. Technical Stack Overview
Language & UI: Kotlin + Jetpack Compose (Material Design, responsive layouts).
Database: MongoDB Atlas for remote, cloud-hosted data storage with scalable CRUD support.
Networking: Retrofit + Moshi for fetching and parsing JSON data.
Authentication: Google OAuth for user sign-in and tracking (optional).
External APIs:


Grocery Prices API (via RapidAPI) for real-time price lookups.
ML Kit Vision for barcode scanning and sensor integration.
Edamam Recipe API for recipe suggestions enhanced with nutritional information.
5. Expected Outcome & Value
Reduced Food Waste: Timely alerts when pantry items are low or nearing expiry, plus smart recipe suggestions to maximize the use of existing ingredients.
Improved Budgeting & Planning: Real-time cost estimates combined with visualized price trends and alerts help users manage spending more effectively.
Enhanced Health & Nutrition: Detailed nutritional information in recipe recommendations supports healthier eating choices.
User-Friendly Experience: An intuitive interface with barcode scanning, dynamic data displays, and responsive design makes grocery management effortless.
6. Conclusion
By combining barcode scanning, real-time pricing with visualized trends and alerts, along with detailed recipe and nutritional recommendations, Smart Grocery Tracker addresses everyday grocery management challenges. Leveraging a robust remote database (MongoDB Atlas) and modern development tools (Kotlin and Jetpack Compose), this app not only simplifies grocery planning but also promotes mindful spending and healthier eating habits—a valuable addition to any busy household’s routine.

