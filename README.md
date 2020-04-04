# 1 gegen 8.5 Millionen

## Ziel

Dieses Projekt ermöglicht die Teilnahme am Gewinnspiel der Sendung "1 gegen 100" mithilfe der API von anti-captcha.com.
Ziel dieses Projektes ist das Ausprobieren des HTTP/2-Clients in Java 11, v.a. im Hinblick auf <b>Asynchronität und funktionale Programmierung</b>.

## Voraussetzungen

In seiner aktuellen Form nimmt das Tool durch das Schicken einer Abfolge von Requests am Gewinnspiel teil mit Nutzerdaten, die im `resources`-Ordner hinterlegt werden müssen.
Das betrifft sowohl den <b>API-Key</b> von [anti-captcha.com](https://anti-captcha.com/), wozu eine Registrierung auf dieser Seite erfolgen und ein Guthaben hinterlegt werden muss, als auch die <b>persönlichen Angaben</b>, die bei der Teilnahme am Gewinnspiel geschickt werden müssen (beispielhaft `HansMüller.java`).
Java 11 wird vorausgesetzt und IntelliJ IDEA empfohlen.

## Ausführung

Zunächst müssen die Libraries von anti-captcha.com kompiliert werden (siehe [anticaptcha-java](https://github.com/AdminAnticaptcha/anticaptcha-java)). Dies geschieht wie folgt:

`javac -cp "libs/*" src/com/anti_captcha/AnticaptchaBase.java src/com/anti_captcha/IAnticaptchaTaskProtocol.java src/com/anti_captcha/Api/ImageToText.java src/com/anti_captcha/Api/NoCaptcha.java src/com/anti_captcha/Api/NoCaptchaProxyless.java src/com/anti_captcha/ApiResponse/BalanceResponse.java src/com/anti_captcha/ApiResponse/CreateTaskResponse.java src/com/anti_captcha/ApiResponse/TaskResultResponse.java src/com/anti_captcha/Helper/DebugHelper.java src/com/anti_captcha/Helper/HttpHelper.java src/com/anti_captcha/Helper/JsonHelper.java src/com/anti_captcha/Helper/StringHelper.java src/com/anti_captcha/Http/HttpRequest.java src/com/anti_captcha/Http/HttpResponse.java`

In IntelliJ müssen jetzt die Libraries in den Classpath des Projekts eingebunden werden. Das geschieht wie folgt:

`Project Structure -> Libraries -> New Project Library -> Java -> Add Libraries (+) -> alles aus lib auswählen -> Selbiges mit libs wiederholen -> OK`

Nun kann in `Run -> Edit Configurations` das Programm `Main.java` über `Add New Configuration (+) -> Application` als `Main class` angegeben und das Programm gestartet werden. Alternativ funktioniert auch der grüne Pfeil links neben dem Klassennamen `Main` oder der neben der main-Methode, da dadurch eine configuration automatisch angelegt wird. In allen drei Fällen wird dabei nämlich der Classpath in der Configuration richtigerweise automatisch auf den des Projekts gesetzt, der im ersten Schritt passend ergänzt wurde. 

## Grenzen

Grenzen des Projekts sind aktuell die <b>mangelnde Fehlertoleranz</b>, d.h. das aktuelle Setup reagiert nicht auf Fehler, um sie zu umgehen / beheben / den Request erneut zu veruschen, sondern bricht die Ausführung ab.
Das bedeutet, dass das Programm nur dann in einem Rutsch durchläuft, wenn kein wesentlicher Fehler auftritt. Zu solchen gehören:
* <i>anti-captcha.com</i>: "API error 2: No idle workers are available at the moment. Please try a bit later or increase your maximum bid in menu Settings - API Setup in Anti-Captcha Customers Area."
* <i>w.srf.ch</i>: "HTTP/1.1 503 Service Unavailable" bei Serverüberlastung

Zudem orientiert sich das Tool an der aktuellen Logik der Requests (Parameter) unter den passenden URLs und an der aktuell verwendeten Captcha-Variante. Änderungen davon würden Tooländerungen erfordern.
Ebenfalls wird ein <b>iPhone X</b> derzeit als User-Agent hartkodiert.

## Disclaimer

Ich bin nicht verantwortlich für jegliche Art von Missbrauch von diesem Tool für nicht rechtmässige Zwecke, z.B. eine Überlastung der Server durch zu viele Requests. Das ist nicht der Zweck des Tools und soll nicht ermutigt werden.