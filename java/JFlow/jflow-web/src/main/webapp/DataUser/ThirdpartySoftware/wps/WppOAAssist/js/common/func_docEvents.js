//切换窗口时触发的事件
function OnWindowActivate() {
    console.log("OnWindowActivate" + "=======================");

    showOATab(); // 根据文件是否为OA文件来显示OA菜单再进行刷新按钮
    setTimeout(activeTab, 2000); // 激活页面必须要页签显示出来，所以做1秒延迟
    return;
}

function OnPresentationBeforeClose(doc) {
    console.log('OnPresentationClose');

    var l_fullName = doc.FullName;
    var l_bIsOADoc = false;
    l_bIsOADoc = CheckIfDocIsOADoc(doc); //判断是否OA文档要关闭
    if (l_bIsOADoc == false) { // 非OA文档不做处理
        return;
    }

    if (doc.Saved == false) { //如果OA文档关闭前，有未保存的数据
        if (wps.confirm("系统文件有改动，是否提交后关闭？" + "\n" + "确认后请按上传按钮执行上传操作。取消则继续关闭文档。")) {
            wps.ApiEvent.Cancel = true;
            return;
        }
    }

    wps.ApiEvent.RemoveApiEventListener("PresentationBeforeClose", OnPresentationBeforeClose);
    doc.Close();
    wps.ApiEvent.AddApiEventListener("PresentationBeforeClose", OnPresentationBeforeClose);
    pSetNoneOADocFlag(l_fullName);
    closeWppIfNoDocument(); // 判断文件个数是否为0，若为0则关闭组件
    wps.FileSystem.Remove(l_fullName);
}

/**
 * 作用：判断当前文档是否从OA来的文档，如果非OA文档（就是本地新建或打开的文档，则设置EnumOAFlag 标识）
 * 作用：设置非OA文档的标识状态
 * @param {*} doc 
 * 返回值：无
 */
function pSetNoneOADocFlag(fullName) {

    var l_param = wps.PluginStorage.getItem(fullName); //定义JSON文档参数
    var l_objParams = new Object();
    if (l_param) {
        l_objParams = JSON.parse(l_param);
    }
    l_objParams.isOA = EnumOAFlag.DocFromNoOA; // 新增非OA打开文档属性
    wps.PluginStorage.setItem(fullName, JSON.stringify(l_objParams)); // 存入内存中
}
/**
 * 权限点集合
 */
var ksoRightsInfo = {
    ksoNoneRight: 0x0000,
    ksoModifyRight: 0x0001,
    ksoCopyRight: 0x0002,
    ksoPrintRight: 0x0004,
    ksoSaveRight: 0x0008,
    ksoBackupRight: 0x0010,
    ksoVbaRight: 0x0020,
    ksoSaveAsRight: 0x0040,
    ksoFullRight: -1
}
var gRightsInfo = ksoRightsInfo.ksoFullRight;
/**
 * 设置文档的权限
 *
 * @param {*} rightsInfo
 * 所有权限： ksoRightsInfo.ksoFullRight
 * 备份权限： ksoRightsInfo.ksoFullRight & ~ksoRightsInfo.ksoBackupRight
 * 另存权限： ksoRightsInfo.ksoFullRight & ~ksoRightsInfo.ksoSaveAsRight
 * 类似保护模式： ksoRightsInfo.ksoFullRight & ~ksoRightsInfo.ksoBackupRight & ~ksoRightsInfo.ksoSaveAsRight & ~ksoRightsInfo.ksoSaveRight & ~ksoRightsInfo.ksoPrintRight & ~ksoRightsInfo.ksoCopyRight
 */
function setDocumentRights(rightsInfo) {
    gRightsInfo = rightsInfo;
    var l_doc = wps.WppApplication().ActivePresentation;
    if (l_doc) {
        l_doc.InvalidateRightsInfo();
        wps.ribbonUI.Invalidate();
    }
}
/**
 * 设置权限的事件实现
 *
 * @param {*} doc
 */
function OnDocumentRightsInfo(doc) {
    var curRightsInfo = wps.ApiEvent.RightsInfo;
    wps.ApiEvent.RightsInfo = gRightsInfo;

}
/**
 * 针对权限的功能可用状态判断
 *
 * @returns
 */
function OnSetSaveAsRightsEnable() {
    if (gRightsInfo & ksoRightsInfo.ksoSaveAsRight)
        return true;
    else
        return false;
}