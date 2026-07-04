# QUICK START: Deploy LocalMart AI in 30 Minutes

## Step 1: Push Code to GitHub (5 min)

```bash
cd c:\newproject

git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/localmart-ai.git
git push -u origin main
```

**Replace `YOUR_USERNAME` with your actual GitHub username.**

---

## Step 2: Set Up Free MySQL Database (5 min)

### Visit Railway
1. Go to https://railway.app
2. Sign up with GitHub (click "Login with GitHub")
3. Click "New Project" → "MySQL"
4. Wait 2-3 minutes for deployment

### Get Database Credentials
1. In Railway dashboard, click on "MySQL" service
2. Go to "Variables" tab
3. **Save these values** (you'll need them in Render):
   - Copy the entire `DATABASE_URL`
   - Or note: `MYSQL_HOST`, `MYSQL_USER`, `MYSQL_PASSWORD`

**Example DATABASE_URL:**
```
mysql://root:password@containers-us-west-123.railway.app:3306/railway
```

---

## Step 3: Deploy to Render (15 min)

### Go to Render
1. Visit https://render.com
2. Click "Sign up" → "Continue with GitHub"
3. Click "Authorize Render"

### Create Web Service
1. Click "+ New" button
2. Select "Web Service"
3. Select `localmart-ai` repository
4. Click "Connect"

### Configure Service
1. Fill in these fields:
   - **Name**: `localmart-ai`
   - **Environment**: Select `Docker`
   - **Region**: Choose closest region
   - **Branch**: `main`

2. Leave other options as default
3. Scroll down to "Environment" section

### Add Environment Variables

Click "Add Environment Variable" and add these one by one:

| Variable Name | Value |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://MYSQL_HOST:3306/MYSQL_DATABASE?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `SPRING_DATASOURCE_USERNAME` | From Railway |
| `SPRING_DATASOURCE_PASSWORD` | From Railway |
| `SPRING_MAIL_USERNAME` | `projectofmca@gmail.com` |
| `SPRING_MAIL_PASSWORD` | `noelvrmwloqtddes` |
| `SPRING_MAIL_FROM` | `projectofmca@gmail.com` |
| `JWT_SECRET` | `E1c9mR7sV2xY8nP4qZ6fB3hL0pW5tU2a` |
| `JWT_EXPIRATION` | `86400000` |
| `SERVER_PORT` | `8080` |
| `SPRING_PROFILES_ACTIVE` | `prod` |

**How to get Railway values:**
- `MYSQL_HOST`: From DATABASE_URL, the hostname part
- `MYSQL_DATABASE`: From DATABASE_URL, the database name (usually `railway`)
- `MYSQL_USER`: `root` (default in Railway)
- `MYSQL_PASSWORD`: From DATABASE_URL, the password part

**Example conversion from DATABASE_URL:**
```
From: mysql://root:xyz123@containers-us-west-123.railway.app:3306/railway

To:
SPRING_DATASOURCE_URL = jdbc:mysql://containers-us-west-123.railway.app:3306/railway?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME = root
SPRING_DATASOURCE_PASSWORD = xyz123
```

### Click "Create Web Service"

Wait 5-10 minutes while Render builds and deploys your app.

---

## Step 4: Get Your Public URL (2 min)

Once deployed (status shows green "Live"):
1. In Render dashboard, click on your service
2. Copy the URL at the top (like `https://localmart-ai.onrender.com`)
3. Save this URL

---

## Step 5: Initialize Database (2 min)

Your app needs database tables. Choose one option:

### Option A: Auto-create (simpler, one-time)
1. In Render dashboard, click "Environment"
2. Find `spring.jpa.hibernate.ddl-auto` 
3. Change it to `create`
4. Click "Save"
5. Render will redeploy (1-2 min)
6. Once live, change it back to `validate`
7. Redeploy again

### Option B: Manual SQL (advanced)
If you have SQL migration files, you can run them manually through Railway's web client.

---

## Step 6: Test Your App (2 min)

1. Copy your Render URL (e.g., `https://localmart-ai.onrender.com`)
2. Open in browser: `YOUR_URL/admin`
3. You should see the admin dashboard
4. Try to login with admin credentials

---

## 🎉 Your App is Live!

### URLs
- **Admin**: `YOUR_URL/admin`
- **Retailer**: `YOUR_URL/retailer`
- **Home**: `YOUR_URL/`

### Helpful Links
- Render Logs: Render dashboard → Logs (check for errors)
- Railway Database: railway.app (check database connection)

---

## Troubleshooting

### "Application won't start"
- Check Render Logs tab
- Verify all env variables are set correctly
- Make sure database is running on Railway

### "Connection refused"
- Verify SPRING_DATASOURCE_URL format
- Check MySQL password is correct
- Ensure Railway MySQL service is running

### "Database tables missing"
- Use Option A above to auto-create tables
- Or run SQL migration scripts manually

---

## Next Steps

1. **Update code**: Make changes locally, push to GitHub
   ```bash
   git add .
   git commit -m "Your changes"
   git push origin main
   ```
   Render will automatically redeploy!

2. **Monitor logs**: Check Render → Logs regularly

3. **Scale if needed**: Upgrade from free tier if app is slow

---

Questions? Check DEPLOYMENT_GUIDE.md for detailed explanations.
