# HabitFlow — offline Android habit tracker

Bu proje Android Studio kullanmadan GitHub üzerinden derlenebilir. Uygulama çalışma sırasında internet kullanmaz; veriler cihazdaki SharedPreferences içinde tutulur.

## Özellikler
- Pazartesi–Pazar gün çubuğu; bugün koyu renkle belirgin.
- Günlük tikler ve tamamlanma yüzdesi.
- Tik / dakika / tekrar hedefleri.
- Süreli hedeflerde yapılan gerçek dakika veya tekrar kaydı.
- 7 günün hangilerinde alışkanlığın yapılacağını seçme.
- İsteğe bağlı bitiş süresi; kapalıysa alışkanlık süresiz.
- Her alışkanlık için ayrı bildirim saati ve seçili günlerde yerel bildirim.
- Aylık 30 günlük genel grafik ve seçilen alışkanlığın kendi grafiği.
- Streak ekranı.
- Sürükle-bırak alışkanlık sıralaması.
- Koyu/açık tema.
- Sol menü + yatay kaydırma ile ana ekran, grafik, streak ve ayarlar ayrı tutulur.
- İnternet/API/backend yok.

## GitHub'dan APK
1. Bu klasörü GitHub repository olarak yükle.
2. **Actions → Build APK → Run workflow** seç.
3. Workflow tamamlanınca **Artifacts → HabitFlow-debug-apk** içinden APK'yı al.

## Not
Bu paket kaynak kodu + GitHub Actions build akışıdır. Bu çalışma ortamında Android SDK/Gradle indirme erişimi olmadığı için burada derlenmiş APK yerine GitHub'ın Android build ortamında üretilecek debug APK akışı hazırlanmıştır.
