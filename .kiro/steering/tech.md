# Technology Stack

## Architecture

**MVVM + Repository Pattern**: UI層、ViewModel層、Repository層、DataSource層の4層アーキテクチャ

データフロー: `UI (Composable) → ViewModel → Repository → DataSource (Local/Network)`

## Core Technologies

- **言語**: Kotlin (JVM Target 1.8)
- **UIフレームワーク**: Jetpack Compose (Material3)
- **DIフレームワーク**: Hilt (Dagger)
- **ビルドツール**: Gradle (Kotlin DSL)
- **最小SDK**: Android 8.0 (API 26)
- **ターゲットSDK**: Android 14+ (API 35)

## Key Libraries

- **Room**: ローカルデータベース (KSPによるコード生成、Kotlin生成モード有効)
- **DataStore**: 設定・認証情報の永続化 (Tinkによる暗号化)
- **Retrofit**: HTTPクライアント (Gsonコンバーター使用)
- **Jsoup**: HTMLパース (大学システムからのデータ抽出)
- **Navigation Compose**: 画面遷移管理
- **Coil**: 画像読み込み

## Development Standards

### Code Quality

- **ktlint**: 自動フォーマットとリントチェック
- コミット前に `./gradlew ktlintCheck` の実行を推奨
- 自動フォーマット: `./gradlew ktlintFormat`

### Dependency Injection

- 全てのRepositoryとDataSourceはHiltで管理
- ViewModel: `@HiltViewModel` アノテーション
- Activity: `@AndroidEntryPoint` アノテーション
- DIモジュール: `application/di/RepositoryModule`, `application/di/SourceModule`

### Database

- Room Entity: `data/*/model/` に配置
- KSP による自動生成 (`room.generateKotlin = true`)
- スキーマディレクトリ: `app/schemas/` (バージョン管理対象外)

## Development Environment

### Required Tools

- Android Studio (Compose対応版)
- JDK 8以上
- Kotlin 1.9+

### Common Commands

```bash
# デバッグビルド
./gradlew assembleDebug

# リリースビルド (keystore環境変数が必要)
./gradlew assembleRelease

# コードフォーマット
./gradlew ktlintFormat

# リントチェック
./gradlew ktlintCheck
```

## Key Technical Decisions

**Repository Pattern採用理由**: データソース（Local/Network）の抽象化により、テスタビリティとメンテナンス性を向上

**DataStoreによる暗号化**: 認証情報はTink libraryで暗号化してDataStoreに保存（SharedPreferences非推奨）

**Jsoup for Scraping**: 大学システムの公式APIが存在しないため、HTMLパースで必要なデータを抽出

**Gson for Navigation Args**: Navigation Composeの引数はGsonでシリアライズして文字列として渡す（Parcelable回避）

---
_created_at: 2026-02-15_
