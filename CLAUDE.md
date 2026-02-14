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
│   └── setting/       # 設定データ
│       ├── SettingRepository.kt / SettingRepositoryImpl.kt
│       ├── model/     # 設定モデル (ThemeSettings等)
│       └── source/    # SettingLocalDataSource (DataStore)
├── ui/                # UI層 (Jetpack Compose)
│   ├── navigation/    # Navigation.kt (NavHost定義), MainNavigationViewModel
│   ├── main/          # メイン画面
│   ├── signin/        # サインイン画面
│   ├── initialize/    # 初期化画面
│   ├── buildingvacancy/   # 建物空き状況画面
│   ├── roomvacancy/       # 教室空き状況画面
│   ├── roomreservation/   # 教室予約画面
│   ├── setting/           # 設定画面
│   └── component/         # 共通コンポーネント
├── theme/             # Composeテーマ定義
└── MainActivity.kt    # エントリーポイント (@AndroidEntryPoint)
```

## レイヤー別の役割

## 1. Application層 (`application/`)
- **DIモジュール**: `RepositoryModule`と`SourceModule`で依存性を提供
- **共通ユーティリティ**: Gson、DateTimeFormatter等の共通インスタンス

## 2. Data層 (`data/`)
各ドメイン（building, room, auth, setting）ごとに以下の構造を持つ：
- **Repository**: データの取得・保存ロジックを抽象化（インターフェース + 実装）
- **DataSource**:
  - `LocalDataSource`: Room Database / DataStore からのデータ読み書き
  - `NetworkDataSource`: Retrofit APIクライアント
- **Model**: データクラス、Entity、DAO

**重要な実装パターン**:
- RoomLocalDataSourceは、HTMLをパースして教室データをRoom DBに保存
- 認証情報はDataStoreに暗号化（Tink使用）して保存
- Repository層でLocal/Networkの切り替えを制御

## 3. UI層 (`ui/`)
- **画面構成**: 各画面は `Screen.kt` + `ViewModel.kt` のペアで構成
- **ナビゲーション**: `Navigation.kt`でNavHostを定義
  - 初期画面: `InitializeScreen` (認証情報確認 → 自動サインイン)
  - サインイン失敗時: `SignInScreen`
  - メイン画面: `MainScreen` (BottomNavigation等)
- **テーマ**: `MainNavigationViewModel`でDataStoreから設定を読み込み、ダークモード切り替え

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