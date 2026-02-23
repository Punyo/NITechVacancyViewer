# プロジェクト概要

NITechVacancyViewerは、名古屋工業大学の教室・建物の空き状況を表示するAndroidアプリケーションです。

# 技術スタック

- **言語**: Kotlin
- **UI**: Jetpack Compose
- **アーキテクチャ**: MVVM + Repository Pattern
- **DI**: Hilt (Dagger)
- **データベース**: Room
- **設定管理**: DataStore (Preferences)
- **ネットワーク**: Retrofit + Jsoup (HTMLパース)
- **コードフォーマット**: ktlint

# 開発コマンド

## ビルド
```bash
# デバッグビルド
./gradlew assembleDebug

# リリースビルド (keystoreが必要)
./gradlew assembleRelease
```

## コードフォーマット
```bash
# ktlintチェック
./gradlew ktlintCheck

# ktlintで自動フォーマット
./gradlew ktlintFormat
```

# アーキテクチャ構成

## 全体構造

```
app/src/main/java/com/punyo/nitechvacancyviewer/
├── application/        # アプリケーション層
│   ├── di/            # Hilt DIモジュール (RepositoryModule, SourceModule)
│   ├── enums/         # 共通列挙型
│   ├── service/       # Accessibility Service (StampAccessibilityService)
│   ├── NITechVacancyViewerApplication.kt  # アプリケーションクラス
│   ├── GsonInstance.kt                     # Gson共通インスタンス
│   └── CommonDateTimeFormater.kt           # 日時フォーマッター
├── data/              # データ層 (Repository + DataSource パターン)
│   ├── building/      # 建物データ
│   │   ├── BuildingRepository.kt / BuildingRepositoryImpl.kt
│   │   ├── model/     # データモデル
│   │   └── source/    # BuildingLocalDataSource (JSONから読み込み)
│   ├── room/          # 教室データ
│   │   ├── RoomRepository.kt / RoomRepositoryImpl.kt
│   │   ├── model/     # Room Entity, DAO, Database
│   │   └── source/    # RoomLocalDataSource (Room DB + HTML パース)
│   ├── auth/          # 認証データ
│   │   ├── AuthRepository.kt / AuthRepositoryImpl.kt
│   │   ├── model/     # 認証関連モデル
│   │   └── source/    # AuthNetworkDataSource (Retrofit), UserCredentialsLocalDataSource (DataStore)
│   ├── setting/       # 設定データ
│   │   ├── SettingRepository.kt / SettingRepositoryImpl.kt
│   │   ├── model/     # 設定モデル (ThemeSettings等)
│   │   └── source/    # SettingLocalDataSource (DataStore)
│   └── widget/        # ウィジェットデータ
│       ├── WidgetRepository.kt / WidgetRepositoryImpl.kt
│       ├── model/     # LaunchError (sealed class + LaunchException)
│       └── source/    # AccessibilityDataSource (AccessibilityChecker実装), WidgetLocalDataSource (同意状態DataStore)
├── ui/                # UI層 (Jetpack Compose)
│   ├── navigation/    # Navigation.kt (NavHost定義), MainNavigationViewModel
│   ├── main/          # メイン画面
│   ├── signin/        # サインイン画面
│   ├── initialize/    # 初期化画面
│   ├── buildingvacancy/   # 建物空き状況画面
│   ├── roomvacancy/       # 教室空き状況画面
│   ├── roomreservation/   # 教室予約画面
│   ├── setting/           # 設定画面
│   ├── accessibility/     # アクセシビリティ同意画面 (AccessibilityConsentScreen + ViewModel)
│   └── component/         # 共通コンポーネント
├── widget/            # Glance ウィジェット層
│   └── stamp/         # 打刻ウィジェット
│       ├── StampWidget.kt         # GlanceAppWidget本体
│       ├── StampWidgetReceiver.kt # GlanceAppWidgetReceiver
│       ├── StampWidgetEntryPoint.kt  # Hilt EntryPoint (ウィジェット用DI)
│       └── OnWidgetTapCallback.kt    # ActionCallback (タップ処理)
├── theme/             # Composeテーマ定義
├── AccessibilityConsentActivity.kt  # 同意用 Activity (@AndroidEntryPoint)
└── MainActivity.kt    # エントリーポイント (@AndroidEntryPoint)
```

## レイヤー別の役割

## 1. Application層 (`application/`)
- **DIモジュール**: `RepositoryModule`と`SourceModule`で依存性を提供
- **共通ユーティリティ**: Gson、DateTimeFormatter等の共通インスタンス

## 2. Data層 (`data/`)
各ドメイン（building, room, auth, setting, widget）ごとに以下の構造を持つ：
- **Repository**: データの取得・保存ロジックを抽象化（インターフェース + 実装）
- **DataSource**:
  - `LocalDataSource`: Room Database / DataStore からのデータ読み書き
  - `NetworkDataSource`: Retrofit APIクライアント
- **Model**: データクラス、Entity、DAO

**重要な実装パターン**:
- RoomLocalDataSourceは、HTMLをパースして教室データをRoom DBに保存
- 認証情報はDataStoreに暗号化（Tink使用）して保存
- Repository層でLocal/Networkの切り替えを制御
- `AccessibilityManager`はfinalクラスのためJVMテストで直接モック不可 → `AccessibilityChecker`インターフェースで抽象化

## 3. UI層 (`ui/`)
- **画面構成**: 各画面は `Screen.kt` + `ViewModel.kt` のペアで構成
- **ナビゲーション**: `Navigation.kt`でNavHostを定義
  - 初期画面: `InitializeScreen` (認証情報確認 → 自動サインイン)
  - サインイン失敗時: `SignInScreen`
  - メイン画面: `MainScreen` (BottomNavigation等)
- **テーマ**: `MainNavigationViewModel`でDataStoreから設定を読み込み、ダークモード切り替え
- **スタンドアロンActivity**: `AccessibilityConsentActivity` はNavHostの外側に存在し、ウィジェットタップ時の初回同意フローを担当

## 4. Glance ウィジェット層 (`widget/`)
- **DIパターン**: ウィジェットはApplicationコンテキストからEntryPointで依存を取得する (`EntryPointAccessors.fromApplication`)
- **状態管理**: `PreferencesGlanceStateDefinition` + `booleanPreferencesKey` でウィジェット状態をDataStoreに保持
- **状態更新**: `updateAppWidgetState()` でPreferencesを更新後、`StampWidget().update()` で再描画
- **ActionCallback**: `OnWidgetTapCallback` がタップを受け取り、同意確認 → アプリ起動 → エラー通知を行う
- **Accessibility Service**: `StampAccessibilityService` が打刻アプリの画面変化を監視し、打刻ボタンを自動クリックする（`pendingAutoClick` フラグでウィジェット起動時のみ動作）

## データフロー

```
UI (Screen)
  ↓ (collectAsStateWithLifecycle)
ViewModel (StateFlow/LiveData)
  ↓ (Hilt inject)
Repository
  ↓
DataSource (Local: Room/DataStore, Network: Retrofit)
```

# コーディング規約

## ktlintルール
- ktlintプラグインが導入されており、コミット前に`ktlintCheck`を実行すること
- 自動フォーマットは`ktlintFormat`で適用可能

## DI (Hilt)
- 新しいRepositoryを追加する場合は`RepositoryModule`に`@Binds`を追加
- 新しいDataSourceを追加する場合は`SourceModule`に`@Provides`を追加
- ViewModelは`@HiltViewModel`でアノテート、Screenで`hiltViewModel()`で取得

## Room Database
- Entityは`data/*/model/`に配置
- DAOインターフェースも同じディレクトリ
- スキーマは`app/schemas/`に自動生成（build.gradle.ktsで設定済み）
- KSPで自動生成されるコードを使用（`room.generateKotlin = true`）

## Jetpack Compose
- 状態管理は`collectAsStateWithLifecycle()`を使用
- ナビゲーションの引数はGsonでシリアライズして文字列として渡す（`GsonInstance.gson`を使用）
- 画面間の一方向遷移は`navigateOneSide()`パターンを使用（backstack削除）

## Glance ウィジェット
- WidgetRepository / Serviceは通常のHilt Componentからではなく、`@EntryPoint`経由でApplicationコンポーネントから注入する
- `Toast`をActionCallbackから表示する場合は `Handler(Looper.getMainLooper()).post { ... }` でメインスレッドに投稿する
- ウィジェット情報は `res/xml/stamp_widget_info.xml` に定義、`res/xml/accessibility_service_config.xml` でServiceを設定する

# リリースビルド

リリースビルドには以下の環境変数が必要です：
- `RELEASE_KEYSTORE_ALIAS`: キーストアのエイリアス
- `RELEASE_KEYSTORE_KEY_PASSWORD`: 鍵のパスワード
- `RELEASE_KEYSTORE_STORE_PASSWORD`: キーストアのパスワード
- キーストアファイル: `app/release.keystore`

GitHub Actionsでは、Secretsから自動的に設定されます。

# ディレクトリ/ファイルの命名規則

- **Screen**: `*Screen.kt` (Composable関数)
- **ViewModel**: `*ViewModel.kt` (HiltViewModel)
- **Repository**: `*Repository.kt` (interface), `*RepositoryImpl.kt` (実装)
- **DataSource**: `*DataSource.kt` (クラス)
- **Model**: `data/*/model/` に配置

# AI-DLC and Spec-Driven Development

Kiro-style Spec Driven Development implementation on AI-DLC (AI Development Life Cycle)

## Project Context

### Paths
- Steering: `.kiro/steering/`
- Specs: `.kiro/specs/`

### Steering vs Specification

**Steering** (`.kiro/steering/`) - Guide AI with project-wide rules and context
**Specs** (`.kiro/specs/`) - Formalize development process for individual features

### Active Specifications
- Check `.kiro/specs/` for active specifications
- Use `/kiro:spec-status [feature-name]` to check progress

## Development Guidelines
- Think in English, generate responses in Japanese. All Markdown content written to project files (e.g., requirements.md, design.md, tasks.md, research.md, validation reports) MUST be written in the target language configured for this specification (see spec.json.language).

## Minimal Workflow
- Phase 0 (optional): `/kiro:steering`, `/kiro:steering-custom`
- Phase 1 (Specification):
  - `/kiro:spec-init "description"`
  - `/kiro:spec-requirements {feature}`
  - `/kiro:validate-gap {feature}` (optional: for existing codebase)
  - `/kiro:spec-design {feature} [-y]`
  - `/kiro:validate-design {feature}` (optional: design review)
  - `/kiro:spec-tasks {feature} [-y]`
- Phase 2 (Implementation): `/kiro:spec-impl {feature} [tasks]`
  - `/kiro:validate-impl {feature}` (optional: after implementation)
- Progress check: `/kiro:spec-status {feature}` (use anytime)

## Development Rules
- 3-phase approval workflow: Requirements → Design → Tasks → Implementation
- Human review required each phase; use `-y` only for intentional fast-track
- Keep steering current and verify alignment with `/kiro:spec-status`
- Follow the user's instructions precisely, and within that scope act autonomously: gather the necessary context and complete the requested work end-to-end in this run, asking questions only when essential information is missing or the instructions are critically ambiguous.

## Steering Configuration
- Load entire `.kiro/steering/` as project memory
- Default files: `product.md`, `tech.md`, `structure.md`
- Custom files are supported (managed via `/kiro:steering-custom`)
