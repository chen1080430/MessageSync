# MessageSync
Message Sync Android Application


**＊＊＊由於目前 MessageSync 還不符合 Play Store 的隱私政策，無法獲得讀取簡訊權限，因此只能透過Telegram API傳送訊息或者驗證Token＊＊＊**

**Beta Version on Play Store**

<a href='https://play.google.com/store/apps/details?id=com.mason.messagesync&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width='400'/></a>


MessageSync 是一個簡訊同步應用，讓使用者可以即時將收到的簡訊轉送到 Telegram 或 LineNotify 上，方便在不同裝置上查看簡訊內容。

</br>

**MessageSync實作簡介：**

架構： MVVM

網路： Retrofit

TelegramApiClass:
負責Telegram Token 驗證API，及Telegram 聊天室傳送的API。

UI:

HomeFragment: 提供用戶測試 API token chatit是否能夠正常使用。

MessageFragment: 顯示所有簡訊的頁面。

SMSBroadcastReceiver: 當有新訊息Receiver會開始運作，由於RECEIVE_SMS的字段太長會被截斷，
因此先對比簡訊夾裡面的新訊息，對比簡訊拿取完整簡訊body，然後把簡訊透過用戶指定API傳送出去。


![CleanShot 2023-06-02 at 22 43 28](https://github.com/chen1080430/MessageSync/assets/32159412/ad2dfeac-3d32-4dc7-b957-294155f2f325)


