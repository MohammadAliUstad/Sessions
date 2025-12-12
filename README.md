<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>Sessions — README</title>
  <style>
    /* Minimal, modern README styling */
    :root{
      --bg:#0f1724;
      --card:#0b1220;
      --muted:#9aa4b2;
      --accent:#7dd3fc;
      --accent-2:#60a5fa;
      --glass: rgba(255,255,255,0.03);
      --radius:12px;
      --max-width:980px;
      --gap:18px;
      font-family: Inter, system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
    }
    html,body{height:100%;margin:0;background:linear-gradient(180deg,#071021 0%, #07172a 100%); color:#e6eef6;}
    .wrap{max-width:var(--max-width);margin:36px auto;padding:28px;background:linear-gradient(180deg, rgba(255,255,255,0.02), rgba(255,255,255,0.01)); border-radius:16px; box-shadow: 0 10px 30px rgba(2,6,23,0.6);}
    header{display:flex;gap:16px;align-items:center;}
    .logo{
      width:72px;height:72px;border-radius:14px;background:linear-gradient(135deg,var(--accent),var(--accent-2));display:flex;align-items:center;justify-content:center;font-weight:700;color:#042a3a;font-size:20px;box-shadow:0 6px 18px rgba(96,165,250,0.12);
    }
    h1{margin:0;font-size:28px;letter-spacing:-0.3px}
    p.lead{margin:6px 0 0;color:var(--muted)}
    .badges{margin-top:12px;display:flex;gap:8px;flex-wrap:wrap}
    .badge{background:var(--glass);padding:6px 10px;border-radius:999px;font-size:13px;color:var(--muted);border:1px solid rgba(255,255,255,0.02)}
    section{margin-top:26px}
    .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:var(--gap)}
    ul.features{list-style:none;padding:0;margin:10px 0 0;display:grid;gap:8px}
    ul.features li{background:linear-gradient(180deg, rgba(255,255,255,0.01), transparent);padding:12px;border-radius:10px;border:1px solid rgba(255,255,255,0.02);color:var(--muted);font-size:15px}
    .screens{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin-top:14px}
    .screenshot{background:#071226;border-radius:10px; padding:6px;border:1px solid rgba(255,255,255,0.03);display:flex;align-items:center;justify-content:center;min-height:160px;overflow:hidden}
    .screenshot img{max-width:100%;max-height:100%;display:block;border-radius:8px;object-fit:cover}
    code.inline{background:rgba(255,255,255,0.03);padding:4px 8px;border-radius:6px;color:var(--accent);font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, "Roboto Mono", monospace}
    pre{background:#071421;padding:14px;border-radius:10px;overflow:auto;border:1px solid rgba(255,255,255,0.02);font-size:13px}
    .cta{display:flex;gap:12px;flex-wrap:wrap;margin-top:14px}
    .btn{background:linear-gradient(90deg,var(--accent),var(--accent-2));padding:10px 14px;border-radius:10px;color:#042a3a;font-weight:600;text-decoration:none}
    .muted{color:var(--muted);font-size:14px}
    footer{margin-top:30px;border-top:1px dashed rgba(255,255,255,0.02);padding-top:18px;color:var(--muted);font-size:13px}
    @media(max-width:880px){ .screens{grid-template-columns:repeat(2,1fr)} }
    @media(max-width:520px){ .screens{grid-template-columns:1fr} header{flex-direction:row;gap:12px} .logo{width:56px;height:56px;font-size:18px}}
  </style>
</head>
<body>
  <div class="wrap" role="main">
    <header>
      <div class="logo">S</div>
      <div>
        <h1>Sessions — Your Focus, Reinvented</h1>
        <p class="lead">A Jetpack Compose Pomodoro timer with cloud-synced session history, Google + Email auth, and Crashlytics stability monitoring.</p>
        <div class="badges" aria-hidden="true">
          <span class="badge">Kotlin</span>
          <span class="badge">Jetpack Compose</span>
          <span class="badge">Material 3</span>
          <span class="badge">Firebase</span>
          <span class="badge">Koin</span>
        </div>
      </div>
    </header>

    <!-- Features -->
    <section>
      <h2>✨ Features</h2>
      <ul class="features">
        <li><strong>Pomodoro Timer</strong> — start customizable focus sessions (25m default) with pause / stop / short and long presets.</li>
        <li><strong>Session Tracking</strong> — completed and partial sessions are logged to Firestore with timestamps.</li>
        <li><strong>Authentication</strong> — Email &amp; Password + Google Sign-In (Firebase Auth).</li>
        <li><strong>Crash Reporting</strong> — Firebase Crashlytics integrated for real-world stability.</li>
        <li><strong>Material 3 UI</strong> — built with Jetpack Compose for smooth, modern UX.</li>
        <li><strong>Koin DI</strong> — lightweight dependency injection for easy testing & scaling.</li>
      </ul>
    </section>

    <!-- UI Highlights -->
    <section>
      <h2>🎨 UI Highlights</h2>
      <div class="grid">
        <div>
          <h3>Focus Timer Screen</h3>
          <p class="muted">Minimal, distraction-free timer with animated circular progress and quick presets.</p>
        </div>
        <div>
          <h3>Sessions History</h3>
          <p class="muted">Cloud-synced list of sessions with duration, completed status, and timestamps.</p>
        </div>
        <div>
          <h3>Authentication Flow</h3>
          <p class="muted">Sleek sign-in screens supporting Google Sign-In and Email/Password onboarding.</p>
        </div>
      </div>
    </section>

    <!-- Screenshots: 6 slots -->
    <section>
      <h2>📸 Screenshots</h2>
      <p class="muted">Replace the <code class="inline">src</code> attributes below with your actual screenshot paths (recommended: <code class="inline">Screenshots/screen1.png</code>, ... , <code class="inline">screen6.png</code>).</p>

      <div class="screens" aria-label="Screenshots">
        <div class="screenshot"><img alt="Screenshot 1" src="Screenshots/screenshot (1).jpg" /></div>
        <div class="screenshot"><img alt="Screenshot 2" src="Screenshots/screenshot (2).jpg" /></div>
        <div class="screenshot"><img alt="Screenshot 3" src="Screenshots/screenshot (3).jpg" /></div>
        <div class="screenshot"><img alt="Screenshot 4" src="Screenshots/screenshot (4).jpg" /></div>
        <div class="screenshot"><img alt="Screenshot 5" src="Screenshots/screenshot (5).jpg" /></div>
        <div class="screenshot"><img alt="Screenshot 6" src="Screenshots/screenshot (6).jpg" /></div>
      </div>
    </section>

    <!-- Technologies -->
    <section>
      <h2>🚀 Technologies Used</h2>
      <div class="grid">
        <div>
          <ul class="features" style="list-style:disc;padding-left:18px;">
            <li><strong>Language:</strong> Kotlin</li>
            <li><strong>UI:</strong> Jetpack Compose + Material 3</li>
            <li><strong>Dependency Injection:</strong> Koin</li>
          </ul>
        </div>
        <div>
          <ul class="features" style="list-style:disc;padding-left:18px;">
            <li><strong>Backend:</strong> Firebase Auth, Firestore</li>
            <li><strong>Monitoring:</strong> Firebase Crashlytics</li>
            <li><strong>Build:</strong> Gradle (Kotlin DSL)</li>
          </ul>
        </div>
      </div>
    </section>

    <!-- Setup -->
    <section>
      <h2>🛠️ Setup &amp; Installation</h2>
      <ol class="features" style="list-style:decimal;padding-left:18px;">
        <li><strong>Clone</strong> the repo:
          <pre>git clone https://github.com/YOUR_USERNAME/Sessions.git
cd Sessions</pre>
        </li>
        <li><strong>Firebase</strong> — place <code class="inline">google-services.json</code> into the <code class="inline">app/</code> folder and enable:
          <ul style="margin:6px 0 0 18px;color:var(--muted)">
            <li>Firebase Authentication (Email/Password + Google)</li>
            <li>Cloud Firestore</li>
            <li>Crashlytics</li>
          </ul>
        </li>
        <li><strong>Gradle</strong> — add required dependencies &amp; plugins (example):
          <pre>// app/build.gradle.kts (excerpt)
plugins {
  id(\"com.android.application\")
  id(\"org.jetbrains.kotlin.android\")
  id(\"com.google.gms.google-services\")
  id(\"com.google.firebase.crashlytics\")
}

dependencies {
  implementation(\"androidx.activity:activity-compose:1.7.2\")
  implementation(\"androidx.compose.material3:material3:1.1.0\")
  implementation(\"io.insert-koin:koin-android:3.4.0\")
  implementation(\"com.google.firebase:firebase-auth-ktx:22.1.0\")
  implementation(\"com.google.firebase:firebase-firestore-ktx:24.9.0\")
  implementation(\"com.google.firebase:firebase-crashlytics-ktx:18.4.0\")
}</pre>
        </li>
        <li><strong>Run</strong> — open in Android Studio, sync Gradle, build and run on a device/emulator.</li>
      </ol>
    </section>

    <!-- Contributing -->
    <section>
      <h2>🌟 Contributing</h2>
      <p class="muted">Contributions are welcome — fork the repo, open issues for bugs or feature requests, and submit pull requests. Consider adding:</p>
      <ul class="features">
        <li>Unit tests for ViewModels &amp; repos</li>
        <li>UI tests for Compose screens</li>
        <li>Extra timer presets &amp; settings</li>
      </ul>
    </section>

    <!-- Contact -->
    <section>
      <h2>📞 Contact</h2>
      <p class="muted">Questions, feedback or collabs? Reach out:</p>
      <div class="cta">
        <a class="btn" href="mailto:Mohammadaliustad@gmail.com">Email Mohammad</a>
        <a class="badge" href="#" style="text-decoration:none;">Open an issue</a>
      </div>
    </section>

    <footer>
      <div>Made with ❤️ to help you focus better — by Yugen Tech</div>
      <div style="margin-top:8px">License: <span class="muted">Add a license (e.g. MIT) in <code class="inline">LICENSE</code> if you want to open-source this project.</span></div>
    </footer>
  </div>
</body>
</html>
