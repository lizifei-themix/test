# FCM Payload Test (package: com.test)

This project is already aligned with the supplied Firebase Android configuration:
`package_name = com.test`.

## Fastest build with GitHub Actions

1. Create a GitHub repository.
2. Upload all files in this project.
3. Push to `main`.
4. Open GitHub -> Actions -> `Build debug APK`.
5. Download artifact `fcm-payload-test-debug`.
6. Extract `app-debug.apk`.
7. Drag the APK into Genymotion Desktop to install.

No local Android SDK is required for this build path.

## Test

Open the app and copy its FCM Token.
Put that token into the Treasure Data test audience and send the Engage Studio push.

The app shows the last received payload and specifically helps check:

- tdCampaignId
- campaignId
- campaign_id
- td_campaign_id

## Security note

This project contains `google-services.json`, which is client-side Firebase configuration.
Do NOT place a Firebase Admin service-account private key in this Android app.
