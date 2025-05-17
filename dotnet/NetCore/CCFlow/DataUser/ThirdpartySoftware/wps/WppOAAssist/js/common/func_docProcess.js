/**
 * 从OA调用传来的指令，打开本地新建文件
 * @param {*} fileUrl 文件url路径
 */
function NewFile(params) {
    //获取WPP Application 对象
    var wppApp = wps.WppApplication();
    var doc = wppApp.Presentations.Add(); //新增OA端文档
    wps.PluginStorage.setItem("IsInCurrOADocOpen", false);

    //检查系统临时文件目录是否能访问
    if (wps.Env && wps.Env.GetTempPath) {
        if (params.newFileName) {
            //按OA传入的文件名称保存
            doc.SaveAs($FileName = wps.Env.GetTempPath() + "/" + params.newFileName);
        } else {
            //OA传入空文件名称，则保存成系统时间文件
            doc.SaveAs($FileName = wps.Env.GetTempPath() + "/OA_" + currentTime());
        }
    } else {
        alert("文档保存临时目录出错！不能保存新建文档！请联系系统开发商。");
    }

    var l_NofityURL = GetParamsValue(params, "notifyUrl");
    if (l_NofityURL) {
        NotifyToServer(l_NofityURL.replace("{?}", "1"));
    }

    //Office文件打开后，设置该文件属性：从服务端来的OA文件
    pSetOADocumentFlag(doc, params);
    //设置当前文档为 本地磁盘落地模式
    DoSetOADocLandMode(doc, EnumDocLandMode.DLM_LocalDoc);
    //强制执行一次Activate事件
    OnWindowActivate();

    return doc; //返回新创建的Document对象
}

/**
 * 打开服务器上的文件
 * @param {*} fileUrl 文件url路径
 */
function OpenFile(params) {
    var l_strFileUrl = params.fileName; //来自OA网页端的OA文件下载路径
    var doc;
    var l_IsOnlineDoc = false; //默认打开的是不落地文档
    if (l_strFileUrl) {
        //下载文档之前，判断是否已下载该文件
        if (pCheckIsExistOpenOADoc(l_strFileUrl) == true) {
            //如果找到相同OA地址文档，则给予提示
            wps.WppApplication().Activate(); //把WPS对象置前
            //根据OA助手对是否允许再次打开相同文件的判断处理
            var l_AllowOADocReOpen = false;
            l_AllowOADocReOpen = wps.PluginStorage.getItem("AllowOADocReOpen");
            if (l_AllowOADocReOpen == false) {
                alert("已打开相同的OA文件，请关闭之前的文件，再次打开。");
                wps.WppApplication().Activate();
                return null;
            } else {
                //处理重复打开相同OA 文件的方法
                var nDocCount = wps.WppApplication().Presentations.Count;
                pReOpenOADoc(l_strFileUrl);
                //重复打开的文档采用不落地的方式打开
                // 不落地方式打开文档判断落地比较多，V1版本先暂时关闭
                l_IsOnlineDoc = true;
                var nDocCount_New = wps.WppApplication().Presentations.Count;
                if (nDocCount_New > nDocCount) {
                    doc = wps.WppApplication().ActivePresentation;
                }
            }
        } else {
            //如果当前没有打开文档，则另存为本地文件，再打开
            if (l_strFileUrl.startWith("http")) { // 网络文档
                DownloadFile(l_strFileUrl, function (path) {
                    if (path == "") {
                        alert("从服务端下载路径：" + l_strFileUrl + "\n" + "获取文件下载失败！");
                        return null;
                    }

                    doc = pDoOpenOADocProcess(params, path);
                    pOpenFile(doc, params, l_IsOnlineDoc);
                });
                return null;
            } else { //本地文档
                doc = pDoOpenOADocProcess(params, l_strFileUrl);
                if (doc)
                    doc.SaveAs($FileName = wps.Env.GetTempPath() + "/" + doc.Name);
            }

        }
    } else {
        //fileURL 如果为空，则按新建OA本地文件处理    
        NewFile(params);
    }

    //如果打开pdf等其他非Office文档，则doc对象为空
    if (!doc) {
        return null;
    }
    pOpenFile(doc, params, l_IsOnlineDoc);

}

function pOpenFile(doc, params, isOnlineDoc){
    var l_IsOnlineDoc = isOnlineDoc

    //Office文件打开后，设置该文件属性：从服务端来的OA文件
    pSetOADocumentFlag(doc, params);
    //设置当前文档为 本地磁盘落地模式
    if (l_IsOnlineDoc == true) {
        DoSetOADocLandMode(doc, EnumDocLandMode.DLM_OnlineDoc);
    } else {
        DoSetOADocLandMode(doc, EnumDocLandMode.DLM_LocalDoc);
    }

    l_NofityURL = GetParamsValue(params, "notifyUrl");
    if (l_NofityURL) {
        l_NofityURL = l_NofityURL.replace("{?}", "1"); //约定：参数为1则代码打开状态
        NotifyToServer(l_NofityURL);
    }
    //重新设置工具条按钮的显示状态
    pDoResetRibbonGroups();
    // 触发切换窗口事件
    OnWindowActivate();
    // 把WPS对象置前
    wps.WppApplication().Activate();
    return doc;
}

/**
 * 不落地打开服务端的文档
 * @param {*} fileUrl 文件url路径
 */
function OpenOnLineFile(OAParams) {
    //OA参数如果为空的话退出
    if (!OAParams) return;

    //获取在线文档URL
    var l_OAFileUrl = OAParams.fileName;
    var l_doc;
    if (l_OAFileUrl) {
        //下载文档不落地
        wps.WppApplication().Presentations.OpenFromUrl(l_OAFileUrl, "OnOpenOnLineDocSuccess", "OnOpenOnLineDocDownFail");
        //设置文档的权限，模拟保护模式打开
        setDocumentRights(ksoRightsInfo.ksoNoneRight)
        l_doc = wps.WppApplication().ActivePresentation;
    }

    //执行文档打开后的方法
    pOpenFile(l_doc, OAParams, true);
    return l_doc;

    // //Office文件打开后，设置该文件属性：从服务端来的OA文件
    // pSetOADocumentFlag(l_doc, OAParams);
    // //设置当前文档为 不落地打开模式
    // DoSetOADocLandMode(l_doc, EnumDocLandMode.DLM_OnlineDoc);
    // // 强制执行一次Activate事件
    // OnWindowActivate();
    // return l_doc;
}

/**
 * 打开在线文档成功后触发事件
 * @param {*} resp 
 */
function OnOpenOnLineDocSuccess(resp) {

}

/**
 * 作用：打开文档处理的各种过程，包含：打开带密码的文档，保护方式打开文档，修订方式打开文档等种种情况
 * params	Object	OA Web端传来的请求JSON字符串，具体参数说明看下面数据
 * TempLocalFile : 字符串 先把文档从OA系统下载并保存在Temp临时目录，这个参数指已经下载下来的本地文档地址
 * ----------------------以下是OA参数的一些具体规范名称
 * docId	String	文档ID
 * uploadPath	String	保存文档接口
 * fileName	String	获取服务器文档接口（不传即为新建空文档）
 * buttonGroups	string	自定义按钮组 （可不传，不传显示所有按钮）
 */
function pDoOpenOADocProcess(params, TempLocalFile) {
    for (var key = "" in params) {
        switch (key.toUpperCase()) //
        {
            case "buttonGroups".toUpperCase(): //按钮组合
                break;
        }
    }

    //可设置ReadOnly?: Kso.KsoMsoTriState属性设置：-1是只读，其他值都是非只读
    var l_Doc = wps.WppApplication().Presentations.Open(TempLocalFile);
    return l_Doc;
}

/**
 *  打开在线不落地文档出现失败时，给予错误提示
 */
function OnOpenOnLineDocDownFail() {
    alert("打开在线不落地文档失败！请尝试重新打开。");
    return;
}

/**
 * 功能说明：判断是否已存在来自OA的已打开的文档
 * @param {字符串} FileURL 
 */
function pCheckIsExistOpenOADoc(FileURL) {
    var l_DocCount = wps.WppApplication().Presentations.Count;
    if (l_DocCount <= 0) return false;

    //轮询检查当前已打开的文档中，是否存在OA相同的文件
    if (l_DocCount >= 1) {
        for (var l_index = 1; l_index <= l_DocCount; l_index++) {
            var l_objDoc = wps.WppApplication().Presentations.Item(l_index);

            var l_strParam = wps.PluginStorage.getItem(l_objDoc.FullName);
            if (l_strParam == null)
                continue;
            var l_objParam = JSON.parse(l_strParam)
            if (l_objParam.fileName == FileURL) {
                return true;
            }
        }
        return false;
    }
}

/**
 * 参数：
 * doc : 当前OA文档的Document对象
 * DocLandMode ： 落地模式设置
 */
function DoSetOADocLandMode(doc, DocLandMode) {
    if (!doc) return;
    var l_Param = wps.PluginStorage.getItem(doc.FullName);
    var l_objParam = JSON.parse(l_Param);
    //增加属性，或设置
    l_objParam.OADocLandMode = DocLandMode; //设置OA文档的落地标志

    var l_p = JSON.stringify(l_objParam);
    //将OA文档落地模式标志存入系统变量对象保存
    wps.PluginStorage.setItem(doc.FullName, l_p);
}

//Office文件打开后，设置该文件属性：从服务端来的OA文件
function pSetOADocumentFlag(doc, params) {
    if (!doc) {
        return;
    }
    var l_Param = params;
    l_Param.isOA = EnumOAFlag.DocFromOA; //设置OA打开文档的标志
    l_Param.SourcePath = doc.FullName; //保存OA的原始文件路径，用于保存时分析，是否进行了另存为操作
    if (doc) {
        var l_p = JSON.stringify(l_Param);
        //将OA文档标志存入系统变量对象保存
        wps.PluginStorage.setItem(doc.FullName, l_p);
    }
}

/**
 * 作用：设置Ribbon工具条的按钮显示状态
 * @param {*} paramsGroups 
 */
function pDoResetRibbonGroups(paramsGroups) {

}

/**
 * 按照定时器的时间，自动执行所有文档的自动保存事件
 */
function OnDocSaveByAutoTimer() {
    var l_Doc;
    var l_Count = 0
    var l_docCounts = wps.WppApplication().Presentations.Count;
    for (l_Count = 0; l_Count < l_docCounts; l_Count++) {
        l_Doc = wps.WppApplication().Presentations.Item(l_Count);
        if (l_Doc) {
            if (pCheckIfOADoc(l_Doc) == true) { // 是否为OA文件
                //执行自动上传到OA服务器端的操作
                pAutoUploadToServer(l_Doc);
                //保存该文档对应的访问过程记录信息
            }
        }
    }
}

/**
 * 实现一个定时器
 */
function OpenTimerRun(funcCallBack) {
    var l_mCount = 0; //设置一个计时器，按每分钟执行一次; 10分钟后重复执行
    var l_timeID = 0; //用于保存计时器ID值

    // 对间隔时间做处理
    var l_AutoSaveToServerTime = wps.PluginStorage.getItem("AutoSaveToServerTime");
    if (l_AutoSaveToServerTime == 0) { // 设置为0则不启动定时器
        l_timeID = wps.PluginStorage.getItem("TempTimerID");
        clearInterval(l_timeID);
        return;
    } else if (l_AutoSaveToServerTime < 3) {
        l_AutoSaveToServerTime = 3;
    }

    l_timeID = setInterval(function () {
        l_mCount = l_mCount + 1;
        if (l_mCount > l_AutoSaveToServerTime) { //l_AutoSaveToServerTime 值由系统配置时设定，见pInitParameters()函数
            l_mCount = 0;
            funcCallBack(); //每隔l_AutoSaveToServerTime 分钟（例如10分钟）执行一次回调函数
        }
    }, 60000); //60000 每隔1分钟，执行一次操作(1000*60)

    wps.PluginStorage.setItem("TempTimerID", l_timeID); //保存计时器ID值
}