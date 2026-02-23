# Project Structure

## Organization Philosophy

**レイヤードアーキテクチャ + ドメイン別分割**

- 上位層（`application/`, `ui/`）は下位層（`data/`）に依存
- 各ドメイン（building, room, auth, setting）は独立したサブパッケージとして管理
- Repository層がデータソースの抽象化境界を担当

## Directory Patterns

### Application Layer (`application/`)
**Location**: `app/src/main/java/com/punyo/nitechvacancyviewer/application/`
**Purpose**: アプリケーション全体の設定、DI、共通ユーティリティ
**Example**:
- `di/RepositoryModule.kt` - Repository の DI バインディング
- `di/SourceModule.kt` - DataSource の DI プロバイダー
- `GsonInstance.kt` - アプリ共通のGsonインスタンス

### Data Layer (`data/{domain}/`)
**Location**: `app/src/main/java/com/punyo/nitechvacancyviewer/data/{domain}/`
**Purpose**: データの取得・永続化ロジック（Repository + DataSource パターン）
**Example**:
- `{Domain}Repository.kt` - インターフェース
- `{Domain}RepositoryImpl.kt` - 実装クラス
- `model/` - Entity, DAO, データクラス
- `source/` - LocalDataSource, NetworkDataSource

各ドメイン: `building`, `room`, `auth`, `setting`

### UI Layer (`ui/{screen}/`)
**Location**: `app/src/main/java/com/punyo/nitechvacancyviewer/ui/{screen}/`
**Purpose**: Jetpack Compose画面とViewModel
**Example**:
- `{Screen}Screen.kt` - Composable関数
- `{Screen}ViewModel.kt` - `@HiltViewModel`付きViewModel
- `component/` - 共通UIコンポーネント（ドメイン非依存）

### Navigation (`ui/navigation/`)
**Location**: `app/src/main/java/com/punyo/nitechvacancyviewer/ui/navigation/`
**Purpose**: NavHostの定義と画面遷移の管理
**Example**:
- `Navigation.kt` - NavHost定義、画面ルート列挙
- `MainNavigationViewModel.kt` - ナビゲーション状態管理

## Naming Conventions

- **Files**: PascalCase (例: `BuildingRepository.kt`, `MainScreen.kt`)
- **Classes**: PascalCase
- **Functions**: camelCase
- **Composable関数**: PascalCase (UIコンポーネント)
- **Repository実装**: `{Domain}RepositoryImpl`
- **DataSource**: `{Domain}LocalDataSource`, `{Domain}NetworkDataSource`
- **Screen**: `{Name}Screen.kt` + `{Name}ViewModel.kt` のペア

## Import Organization

```kotlin
// 標準ライブラリ
import kotlin.collections.*

// Android Framework
import android.content.Context
import androidx.compose.runtime.*

// Jetpack Libraries
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.*

// Third-party
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.http.GET

// アプリ内モジュール（絶対パス）
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import com.punyo.nitechvacancyviewer.ui.component.LoadingIndicator
```

**Path Aliases**: 使用していない（フルパッケージ名を使用）

## Code Organization Principles

### Dependency Flow
```
UI → ViewModel → Repository → DataSource
```

- UI層はViewModelのみに依存
- ViewModelはRepositoryインターフェースに依存（実装に非依存）
- RepositoryはDataSourceに依存
- **逆方向の依存は禁止**（下位層が上位層を参照してはならない）

### DI管理原則

- Repositoryは `@Binds` でインターフェースと実装をバインド
- DataSourceは `@Provides` で具体的なインスタンスを提供
- ViewModelは `@HiltViewModel`、画面では `hiltViewModel()` で取得

### データ永続化戦略

- **構造化データ**: Room Database（Entity + DAO）
- **Key-Value設定**: DataStore Preferences
- **認証情報**: DataStore + Tink暗号化
- **一時キャッシュ**: ViewModel内のStateFlow/LiveData

### Navigation パターン

- 画面引数はGsonでシリアライズして文字列として渡す
- 一方向遷移（戻らせたくない場合）: `popUpTo` でバックスタック削除
- 初期フロー: `InitializeScreen` → 認証確認 → `SignInScreen` or `MainScreen`

---
_created_at: 2026-02-15_
