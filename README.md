# AudioNotes
Заготовка взята с моего приложения для простых текстовых заметок, к нему я прикручу Azure Recognition Services
В данном проекте используется база данных SQLite, SDK cognitive-services-speech-sdk от Microsoft
По нажатию на кнопку записывается речь, параллельно с этим с помощью класса MediaRecorder записывается аудиофайл, который сохраняется локально на устройстве и потом может воспроизводиться.Затем голос отправляется на сервер, приходит ответ с текстом, заносится в БД и отображается в ListView. При нажатии на элемент списка ListView воспроизводится аудиофайл, привязанный к данной записи(если он имеется)
Также имеется возможность выбрать один из 4 языков записи с помощью Spinner
