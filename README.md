# AiModelsCompete: OpenRouter Model Explorer

AiModelsCompete is a professional Android benchmarking tool designed to explore, test, and compare AI models via the [OpenRouter API](https://openrouter.ai/). It focuses on providing a high-performance, offline-first experience specifically for **free-tier models**.

## 🎯 Project Vision
To provide a centralized platform where developers and AI enthusiasts can benchmark free AI models, ensuring they find the best balance of speed, cost (zero), and output quality without manual switching.

## ✨ Features

### Current (MVP)
*   **Model Explorer**: Browse the full catalog of OpenRouter models with a focus on free-tier availability.
*   **AI Playground**: Real-time chat interface to test model responses.
*   **Performance Benchmarking**: Automatic tracking of **Average Latency (ms)** and **Throughput (tokens/sec)**.
*   **Local History**: Persistent storage of all chat interactions.
*   **Favorites**: Quick access to your most-used models.
*   **Strict Cost Filtering**: Numerical validation to ensure only $0.00 cost models are displayed in the "Free" view.

### 🗺️ Roadmap
*   **Phase 2**: Side-by-side model comparison and detailed benchmark charts.
*   **Phase 3**: Global leaderboards and community benchmarks.
*   **Phase 4**: Advanced team features and monetization strategies.

## 🏗️ Architecture & Tech Stack

The project is built using **MVVM + Clean Architecture** and **MVI** patterns to ensure scalability and testability.

| Layer | Responsibility | Tech / Libraries |
| :--- | :--- | :--- |
| **Presentation** | UI & State Management | Jetpack Compose, Material 3, ViewModels |
| **Domain** | Business Logic & Use Cases | Kotlin Coroutines, Flow |
| **Data** | Persistence & Networking | Room, Retrofit, DataStore, Gson |

### Key Technologies:
- **Dagger Hilt**: Dependency Injection.
- **Room**: Offline-first local database.
- **Retrofit**: Type-safe HTTP client for OpenRouter API.
- **DataStore**: Secure preference storage (e.g., API Keys).
- **Kotlinx Coroutines/Flow**: Reactive programming and background processing.

## 🗄️ Database Design
The app uses a Room database with three main entities:
1.  **Models**: Caches model metadata (id, name, context length, pricing).
2.  **History**: Stores prompt/response pairs linked to specific model IDs.
3.  **Benchmarks**: Aggregates performance data for leaderboard calculations.

## 🚦 Getting Started

1.  **API Key**: Obtain a free API key from [OpenRouter Keys](https://openrouter.ai/keys).
2.  **Configuration**: Enter the key in the app's **Settings** screen.
3.  **Sync**: Use the **Pull-to-Refresh** gesture on the Models screen to fetch the latest catalog.
4.  **Test**: Select a model and start chatting in the Playground to generate benchmark data.

## 📜 License
MIT License - see [LICENSE](LICENSE) for details.
