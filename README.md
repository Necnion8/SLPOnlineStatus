# SLPOnlineStatus
BungeeCordプラグイン

## Description
  デフォルトサーバーのオンラインステータスをチェックし、<br>
  のServerListPlusのプレースホルダに `%server_status%` を追加する。<br>
  
  チェックを行う対象のサーバーは BungeeCord config.yml 内の listeners.priorities 設定の最初のサーバー。
  ※ BungeeCord 環境のみ

## Requirement
- BungeeCord
- ServerListPlus (BungeeCord)

## Setup
1. このプラグインとServerListPlusをBungeeCordに導入する
2. `%server_status%` を ServerListPlus の好きなmotd設定に書き加える
3. 設定を反映させたい場合は、`/greload` を実行する

## Licence
[MIT](https://github.com/kotabrog/ft_mini_ls/blob/main/LICENSE)
