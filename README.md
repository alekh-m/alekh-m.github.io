# Tic Tac Toe Website

This repository hosts a simple Tic Tac Toe web app with:

- Frontend: HTML, CSS, JavaScript
- Backend: Java (Spring Boot)
- Modes:
  - Play with computer
  - Play with another player

## Project Structure

- `index.html` - UI markup
- `style.css` - UI styling
- `script.js` - frontend game logic and API calls
- `backend/` - Spring Boot backend API
- `.github/workflows/ci-cd.yml` - GitHub Actions pipeline

## Deploy Behavior

The GitHub Actions workflow runs on pushes to `main` and does two jobs:

1. `build-backend`
   - Uses GitHub-hosted Ubuntu runner
   - Installs Java 17 (Temurin)
   - Builds backend with Maven
2. `deploy-frontend`
   - Copies `index.html`, `style.css`, and `script.js` into a deployment folder
   - Deploys those static files to GitHub Pages

The backend is only built/validated in CI; it is not automatically deployed by this workflow.

## Configure Frontend API URL (Production)

By default, frontend calls:

- `http://localhost:8080/api/game`

For production, set a custom API base URL in `index.html` before loading `script.js`:

```html
<script>
  window.TIC_TAC_TOE_API_BASE = "https://your-backend-domain.com/api/game";
</script>
<script src="script.js"></script>
```

Update `https://your-backend-domain.com` to your real backend host.

## Run Locally

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

- Open `index.html` directly, or serve the repo root with any static file server.
- Ensure backend is reachable at the configured API base URL.
