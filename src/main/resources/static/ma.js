(function () {
    var maxSize = 3;
    var params = {};
    var collectDataPath = "";
    if (!collectDataPath) {
        var protocol = window.location.protocol;
        var host = window.location.host;
        collectDataPath = protocol + "//" + host;
    }
    //Document对象数据
    if (document) {
        params.domain = document.domain || ''; //获取域名
        params.url = document.URL || '';       //当前Url地址
        params.title = document.title || '';
        params.referrer = document.referrer || '';  //上一跳路径
    }
    //navigator对象数据
    if (navigator) {
        params.language = navigator.language || '';    //获取所用语言种类
    }
    var i;
    var els = document.getElementsByTagName('button');
    for(i=0 ; i<els.length ; i++){
        if (els[i].addEventListener) {
            els[i].addEventListener("click", processEvent, false);
        } else if (els[i].attachEvent) {
            els[i].attachEvent("onclick", processEvent, false);
        }

    }
    var j;
    var elsInput = document.getElementsByTagName('input');
    for(j=0 ; j<elsInput.length ; j++){
        if (elsInput[j].addEventListener) {
            elsInput[j].addEventListener("click", processEvent, false);
        } else if (elsInput[j].attachEvent) {
            elsInput[j].attachEvent("onclick", processEvent, false);
        }
    }
    var clickArr = [];
    //点击事件监听
    function processEvent(e){
        var target = e.target;
        var nodeName = target.nodeName;
        switch (nodeName) {
            case "BUTTON":
                var temp = {};
                temp.clickName = target.innerHTML;
                temp.href = "";
                clickArr.push(temp);
                break;
            case "INPUT":
                var inputType = target.type;
                if ("button" == inputType) {
                    var temp = {};
                    temp.clickName = target.value;
                    temp.href = "";
                    clickArr.push(temp);
                }
                break;
        }
        if (clickArr.length > maxSize) {
            // 攒够maxSize次发送
            console.log(clickArr);
            params.clickEvents = clickArr;
            params.requetTime = new Date().getTime();
            sendData(params);
            //重置点击事件集合
            clickArr = [];
        }

    }
    function sendData(params) {
        //通过伪装成Image对象，请求后端脚本
        var img = new Image(1, 1);
        var jsonArgs = JSON.stringify(params);
        var src = collectDataPath + '/dataCollection/log.gif?args=' + encodeURIComponent(jsonArgs);
        console.log(src);
        img.src = src;
    }
    $(document).ready(function(){
        //通过伪装成Image对象，请求后端脚本
        var img = new Image(1, 1);
        params.clickEvents = [];
        params.requetTime = new Date().getTime();
        sendData(params);
    });
})();