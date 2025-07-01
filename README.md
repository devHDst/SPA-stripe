<h1>SPA バックエンド(決済管理)</h1>

#目次

<p>0.操作方法</p>
<p>1.プロジェクトの概要</p>
<p>2.プロジェクトの作成経緯</p>
<p>3.構成</p>
<p>4.改善課題</p>

<h2>0.操作方法</h2>
<p>ローカルのでの運用方法はルートディレクトリでvscode+jdkの構成で実行していたので、vscode上部の検索バーから「デバッグを選択」</p>
<p>注意としてstripe_api_keyとgoogle_map_keyは個別の値を差し込むこと(サーバー上での運用手順は追記予定)</p>

<h2>1.プロジェクトの概要</h2>
<p>react+springbootで予約、自動決済機能を活用したSPA(single-page-Application)であり、このrepositoryは以下バックエンド枠のstriep管理コンテナの管理兼メモとして利用してます。</p>
<p>現在の進捗としてローカルでの挙動まで確認し、今後2ヶ月の目標としてサイトとして一般的にブラウザで利用できるところまで作成する予定です。</p>

<img width="800" alt="構成" src="https://github.com/user-attachments/assets/312c6f5f-8c8e-4421-9868-f2447734a15f" />

<h2>2.プロジェクトの作成経緯</h2>
<p>元の題材としてはweb系で何かECサイトをコピーしてみるというところで形にならなかった中で、行きつけのお店が予約時に決済をとるアプリ機能を採用していたが、</p>
<p>レビューで予約直後の決済はキャンセルできない不満が散見されたので、もし当事者ならどのように改善したかという発想をベースにSPAとして切り出してみただけです。</p>
<p>上記の構成図みて</p>
<p>・mqいらなくない？</p>
<p>・なんでコンテナ分けたの?　1個のコンテナでバッチとして運用すればよくない？</p>
<p>と疑問を思われると思いますが、全くその通りだと思います。こちらに関してはspring未経験で学習目的なので慣れるために色々機能使いたかっただけです。納品するプロジェクト業務ではちゃんと1つにまとめます。</p>

<h2>3.構成</h2>
<h3>3-1.開発環境</h3>
<pre>
| 言語・主にインストールしたlib | バージョン |
| --------------------- | ---------- |
| java                  | 17         |
| spring-boot           | 3.5.0-M3   |
| stripe-java           | 10.1.0     |
| spring-cloud-starter-task| 3.3.0-M3|
| myqsl                 | 8.0.42     |
| spring                | 8.5.3      |

</pre>

<h3>3-2.ディレクトリ</h3>
<pre>
~/main
  ├── java
  │   └── com
  │       └── restapi
  │           └── paymentcontrol
  │               ├── Controller
  │               │   └── StripeInfoController.java
  │               ├── model
  │               │   ├── entity
  │               │   │   └── StripeInfo.java
  │               │   └── repository
  │               │       └── StripeRepository.java
  │               ├── Payload(リクエストパラメータ管理)
  │               │   ├── StripePayload.java
  │               │   └── StripePrePayload.java
  │               ├── PaymentcontrolApplication.java
  │               ├── Security
  │               │   └── SecurityConfig.java
  │               ├── service(Stripeと直接やりとりするメソッド)
  │               │   └── PaymentService.java
  │               └── task(受信キュー周りの設定)
  │                   ├── PaymentPackage.java
  │                   ├── PaymentTaskRunner.java
  │                   └── RabbitMQConfig.java
  └── resources
      ├── application.properties
      └── logback-spring.xml
</pre>

<h2>4.改善課題</h2>
<p>1.予約管理コンテナから単方向でキューを送って貰っているが stripe側のステータスを管理するために逆方向からの実装と詳細な予約管理を実装をする</p>
<p>2.最初の構成の際に誤ってspring-securityも取り込んだため、アクセス制限制御を暫定外しているが、サイト公開までにアクセス制御の実装をする</p>
