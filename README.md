# SMSObserver
短信监听

短信，广播，jar冲突

根据需求，需要做一个监听短信广播，收到短信后，加密发送到后台的需求。 
遇到困难1.后台加密类使用aplich的库与framework层的aplich的库冲突了（通过修改jar的包名解决）； 
2.获取本机手机的号码，无法解决 
特此记录