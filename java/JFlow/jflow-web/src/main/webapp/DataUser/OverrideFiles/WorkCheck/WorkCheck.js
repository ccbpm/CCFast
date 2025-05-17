/* $(function () {

 //签批组件专用方法.
    if(typeof frmWorkCheck != "undefined"){
         if (frmWorkCheck.SigantureEnabel == 3 || frmWorkCheck.SigantureEnabel == 4 || frmWorkCheck.SigantureEnabel == 5) {
             loadStamp_Init();
         }
     }

 })*/
var datas = new Array();//存储要回显的签名值
var signMethodsDatas = ["SM3WithSM2", "SM3WithSM2", "SM3WithSM2", "SM3WithSM2", "SM3WithSM2", "SM3WithSM2", "SM3WithSM2","SM3WithSM2", "SM3WithSM2", "SM3WithSM2", "SM3WithSM2", "SM3WithSM2", "SM3WithSM2", "SM3WithSM2"];
/**
 * 调用各项目各自电子签字代码
 */
function Siganture() {

}

/**
 * 动态加载签章JS及初始化数据
 */
function loadStamp_Init() {
    Skip.addJs("/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/bjca/BJCAWebSign.js?ver=" + Math.random());
    Skip.addJs("/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/public/jquery-1.11.3.min.js?ver=" + Math.random());
    Skip.addJs("/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/public/echarts/echarts.min.js?ver=" + Math.random());
    Skip.addJs("/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/public/drag.js?ver=" + Math.random());
    Skip.addJs("/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/public/jquery.jqprint-0.3.js?ver=" + Math.random());
    Skip.addJs("/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/doc/lab.js?ver=" + Math.random());
    Skip.addJs( "/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/bjca/BJCAWebSignForWindows.js?ver=" + Math.random());
    Skip.addJs( "/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/bjca/config.js?ver=" + Math.random());
    Skip.addJs( "/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/bjca/PopMenu.js?ver=" + Math.random());
    Skip.addJs( "/DataUser/OverrideFiles/WorkCheck/qianzhang/static/js/bjca/BJCAWebSignCallback.js?ver=" + Math.random());
    BWS_Init();
//debugger;
    if (typeof datas !="undefined" && datas.length != 0) {

        showSign();
        //showSignStamp();
        setTimeout(function(){showSignStamp();},4000) ;
    }
}
/**
 * 自定义签名按钮
 * @param {any} track 当前审核的信息
 * @param {any} isEditWorkCheck 是否是可以编辑的审核信息
 * @param {any} idx 当前的顺序
 */
function GetSigantureSelf(track, isEditWorkCheck, idx) {
    if (isEditWorkCheck == false) {
        if (track.WritImg != null && track.WritImg != "" && track.WriteStamp != null && track.WriteStamp != "") {
            datas.push({
                WriteImg: track.WritImg.replace(/' '/, ''),
                Idx: idx
            })
            stampIdx++;
        }
        return "";
    }
    retHtml += "<div class='verifyedgif20' id = 'verifyedgif20' style = 'position: relative;' ></div ><a href='javascript:positionSign(20,0)'>盖章</a><div > <div style='display:none'><textarea id = 'signatureData20' name = 'signatureData20' style = 'FONT-SIZE: 12pt; WIDTH: '100%;' COLOR: '#000000;' FONT-FAMILY: '仿宋_GB2312; HEIGHT: 155px' rows = '5'  cols = '75' placeholder = '显示签章值区域' readonly = 'readonly' ></textarea > </div ></div > ";
    return retHtml;
}
/**
 * 自定义签章按钮
 * @param {any} track 当前审核的信息
 * @param {any} isEditWorkCheck 是否是可以编辑的审核信息
 * @param {any} idx 当前的顺序
 */
function GetStamp(track, isEditWorkCheck, idx) {
    if (isEditWorkCheck == false) {
        if (track.WritImg != null && track.WritImg != "" && track.WriteStamp != null && track.WriteStamp != "") {
            datas.push({
                WriteStamp: track.WriteStamp.replace(/' '/, ''),
                Idx: idx
            })
            stampIdx++;
        }
        return "";
    }
    var retHtml = "<div class='verifyedgif10' id='verifyedgif10' style='position: relative;'></div><a href='javascript:positionSign(10,0)'>签字</a> <div style='display:none'><textarea id = 'signatureData10' name = 'signatureData10' style = 'FONT-SIZE: 12pt; WIDTH: '100%;' COLOR: '#000000;' FONT-FAMILY: '仿宋_GB2312; HEIGHT: 155px' rows = '5'  cols = '75' placeholder = '显示签名值区域' readonly = 'readonly' display='none' ></textarea > </div ></div >";
    return retHtml;
}
/**
 * 签名+签章
 * @param {any} track 当前审核的信息
 * @param {any} isEditWorkCheck 是否是可以编辑的审核信息
 * @param {any} idx 当前的顺序
 */
function GetSigantureStampSelf(track, isEditWorkCheck, idx) {
   // debugger;
    if (track.WritImg != null && track.WritImg != "" && track.WriteStamp != null && track.WriteStamp != "") {
        datas.push({
            WriteImg: track.WritImg.replace(/' '/, ''),
            WriteStamp: track.WriteStamp.replace(/' '/, ''),
            Idx: isEditWorkCheck==true?0:idx,
            isEditWorkCheck:isEditWorkCheck
        })
        stampIdx++;
    }else   if ((track.WritImg != null && track.WritImg != "") && (track.WriteStamp == null || track.WriteStamp == "")) {
        datas.push({
            WriteImg: track.WritImg.replace(/' '/, ''),
            WriteStamp: '',
            Idx: isEditWorkCheck==true?0:idx,
            isEditWorkCheck:isEditWorkCheck
        })
        stampIdx++;
    }else   if ((track.WritImg == null || track.WritImg == "") && (track.WriteStamp != null && track.WriteStamp != "")) {
        datas.push({
            WriteImg:  '',
            WriteStamp: track.WriteStamp.replace(/' '/, ''),
            Idx: isEditWorkCheck==true?0:idx,
            isEditWorkCheck:isEditWorkCheck
        })
        stampIdx++;
    }
    if (isEditWorkCheck == false) {
        return "";
    }

    var retHtml = "<div class='verifyedgif10' id='verifyedgif10' style='position: relative;'></div><a href='javascript:positionSign(10,0)'>签字</a> <div style='display:none'><textarea id = 'signatureData10' name = 'signatureData10' style = 'FONT-SIZE: 12pt; WIDTH: '100%;' COLOR: '#000000;' FONT-FAMILY: '仿宋_GB2312; HEIGHT: 155px' rows = '5'  cols = '75' placeholder = '显示签名值区域' readonly = 'readonly' display='none' ></textarea > </div ></div >";
    retHtml += "<div class='verifyedgif20' id = 'verifyedgif20' style = 'position: relative;' ></div ><a href='javascript:positionSign(20,0)'>盖章</a><div > <div style='display:none'><textarea id = 'signatureData20' name = 'signatureData20' style = 'FONT-SIZE: 12pt; WIDTH: '100%;' COLOR: '#000000;' FONT-FAMILY: '仿宋_GB2312; HEIGHT: 155px' rows = '5'  cols = '75' placeholder = '显示签章值区域' readonly = 'readonly' ></textarea > </div ></div > ";
    return retHtml;
}
/**
 * 签章回显
 **/
function showSign() {
//debugger;
    var i = 0;
    var j = 0;

    var showSignInterval = setInterval(function () {
        //alert("进入showSign");
        if ($_$WebSocketConnectState) {

            i++;
            if (i > datas.length) {
                clearInterval(showSignInterval);
                return
            }
            var isEditWorkCheck = datas[i - 1].isEditWorkCheck;
            var _Idx = datas[i - 1].Idx;
            /*if (isEditWorkCheck==true){
                _Idx = datas[i - 1].Idx-1;
            }*/
            j++;
            $_$CurOrgData[i] = document.getElementById("WorkCheck_Doc" + _Idx).innerHTML;//"同意";//
            $_$CurSignData[i] = datas[i - 1].WriteImg;
            var orgdata = $_$CurOrgData[i];
            var signdata = datas[i - 1].WriteImg;//$_$CurSignData[i][0];
            if(signdata != null && signdata!=""){
                BWS_Verify(orgdata, signdata, function (ret) {
                    //alert(ret.retVal);
                    if (ret.retVal) {
                        //显示验证后的印章图片
                        ESeaL_CreateSignMenu(j);
                        $_$CurSignMethod[j] = signMethodsDatas[j - 1];
                        var temp = $ShowSignCallback(j, ret.retVal);
                        //alert(temp);
                        document.getElementById("verifyedgif1" + _Idx).innerHTML = temp;
                        if(document.getElementById("signatureData1" + _Idx) != null){
                            document.getElementById("signatureData1" + _Idx).value = signdata;
                        }

                        //禁用印章div右键菜单
                        document.getElementById("verifyedgif1" + _Idx).oncontextmenu = function () {
                            return false;
                        }
                    }
                }, false);
            }


           /* var signdata2 = datas[i - 1].WriteStamp;// $_$CurSignData[i][1];
            if(signdata2 != null && signdata2!=""){
                BWS_Verify(orgdata, signdata2, function (ret) {
                    if (ret.retVal) {

                        //显示验证后的印章图片
                        ESeaL_CreateSignMenu(j);
                        $_$CurSignMethod[j] = signMethodsDatas[j - 1];
                        var temp = $ShowSignCallback(j, ret.retVal);
                        document.getElementById("verifyedgif2" + _Idx).innerHTML = temp;
                        if(document.getElementById("signatureData2" + _Idx) != null){
                            document.getElementById("signatureData2" + _Idx).value = signdata2;
                        }
                        //禁用印章div右键菜单
                        document.getElementById("verifyedgif2" + _Idx).oncontextmenu = function () {
                            return false;
                        }
                    }
                }, false);
            }*/
        }
    }, 800);

}
function showSignStamp() {

    var ii = 0;
    var jj = 0;

    var showSignInterval = setInterval(function () {
        //alert("进入showSign");
        if ($_$WebSocketConnectState) {
            ii++;
            if (ii > datas.length) {
                clearInterval(showSignInterval);
                return
            }
            var isEditWorkCheck = datas[ii - 1].isEditWorkCheck;
            var _Idx = datas[ii - 1].Idx;
            jj++;
            $_$CurOrgData[ii] = document.getElementById("WorkCheck_Doc" + _Idx).innerHTML;//"同意";//
            $_$CurSignData[ii] = datas[ii - 1].WriteImg;
            var orgdata = $_$CurOrgData[ii];
            var signdata2 = datas[ii - 1].WriteStamp;// $_$CurSignData[i][1];
                 if(signdata2 != null && signdata2!=""){
                     BWS_Verify(orgdata, signdata2, function (ret) {
                         if (ret.retVal) {
                             //显示验证后的印章图片
                             ESeaL_CreateSignMenu(jj);
                             $_$CurSignMethod[jj] = signMethodsDatas[jj - 1];
                             var temp = $ShowSignCallback(jj, ret.retVal);
                             document.getElementById("verifyedgif2" + _Idx).innerHTML = temp;
                             if(document.getElementById("signatureData2" + _Idx) != null){
                                 document.getElementById("signatureData2" + _Idx).value = signdata2;
                             }
                             //禁用印章div右键菜单
                             document.getElementById("verifyedgif2" + _Idx).oncontextmenu = function () {
                                 return false;
                             }
                         }
                     }, false);
                 }
        }
    }, 800);

}

/**************************通用的方法*******************************/




/**
 * 调用各项目各自盖章代码
 */
function positionSign(stampID, docidx) {
    positionSign(stampID, idx);
}
//第一步，点击签章按钮，根据平台是win还是Linux决定是否弹窗
function positionSign(stampID, docidx) {
    //debugger;
    $_$CurStampID = stampID;
    if ($_$is_windows) {
        positionSignFunc(stampID, docidx);
    } else {
        showLogin(positionSignFunc(stampID, docidx));
    }
}
//签章
function positionSignFunc(stampID, docidx) {
//debugger;
    var orgdata = document.getElementById("WorkCheck_Doc" + docidx).value;
    var strSealID = $_$CurSealID[$_$CurStampID];
    var strCertID = $_$CurCertID[$_$CurStampID];
    if (orgdata == "") {
        alert("请输入原文！");
        return;
    }
    if (!$_$is_windows) {
        if (strCertID == "") {
            alert("请选择证书！");
            return;
        }
        if (strSealID == "") {
            alert("请选择印章！");
            return;
        }
    }
    $_$CurOrgData[$_$CurStampID] = orgdata;

    // 右键菜单
    // @param stampID 印章DIV的id
    ESeaL_CreateSignMenu($_$CurStampID);
    BWS_DirectSign(strCertID, strSealID, orgdata, function (ret) {
        if (ret.signData) {
            //显示验证后的印章图片
            var retObj = {
                retVal: ret.signData,
                picData: ret.picData
            };
            var temp = $DirectSignCallback(retObj);
            document.getElementById("verifyedgif" + $_$CurStampID).innerHTML = temp;
            //禁用印章div右键菜单
            document.getElementById("verifyedgif" + $_$CurStampID).oncontextmenu = function () {
                return false;
            }
            // console.log("done");
        }
    });

    $("#qm" + $_$CurStampID).css("display", "none");
}

//验章
function btnVerify(stampID) {
    // var orgdata = $_$CurOrgData[stampID];
    var orgdata = document.getElementById("originData1").value;
    $_$CurOrgData[stampID] = orgdata;
    var signdata = $_$CurSignData[stampID];
    if (signdata == "") {
        alert("请先签章！");
        return;
    }
    if (orgdata == "") {
        alert("原文不能为空！");
        return;
    }
    $_$CurStampID = stampID;
    BWS_Verify(orgdata, signdata, function (ret) {
        if (ret.retVal) {
            //显示验证后的印章图片
            var retObj = { retVal: ret.retVal };
            $VerifyCallback(retObj);
        }
    }, true);
}

/**
 * 批量盖章
 * @constructor
 */
var stampType="1";
function BatchStamp(type){
    stampType = type;
    //获取选择的行数
    var isHave = false;
    var workid = 0;
    $("input[type='checkbox']:checked").each(function () {
        var id = $(this).attr('id');
        if (id != null && id != undefined) {
            isHave = true;
            workid = id.replace("CB_", "");
            return false;
        }

    });
    if (isHave == false) {
        alert("请选择批处理的待办数据");
        return;
    }
    var url ="../../../DataUser/OverrideFiles/WorkCheck/BatchStamp2.html?FK_Node="+nodeID;
    /* var v = window.showModalDialog(url, 'stamp', 'scrollbars=no;resizable=no;center=yes;minimize:yes;maximize:yes;dialogHeight: 450px; dialogWidth: 350px; dialogTop: 100px; dialogLeft: 150px;');
     if (v == '1')
         return true;*/
    // var newWindow = window.open(url, '证书登录', 'width=400,height=200,top=300,left=300,scrollbars=no,resizable=no,toolbar=no,status=no,location=no,center=yes,center: yes;');
    // newWindow.focus();
    //OpenEasyUiDialogExt(url, '证书登录', 800, 500, false);
    init(function(){
        SetUserCertList("LoginForm.UserList", CERT_TYPE_HARD);
    },function(){
        alert("fail");
    });
    $('#batchStampDiv').modal().show();
}

function call_back(data){

    if(data.retVal){

        booleanValue=data.retVal;

        alert(data.retVal+'PIN码校验成功');

        ukeyoryqd();
        $('#batchStampDiv').modal('hide');
    }else {

        alert(data.retVal+'PIN码校验失败');
        $('#batchStampDiv').modal('hide');
    }

}
function call_back1(data){

    var lastCertid = data.retVal;
    //alert("SOF_GetDeviceInfo："+lastCertid);

    if(lastCertid=="GBUKEY"){
        getUserCertForUkey();
    }
    if(lastCertid=="USBKEY"){
        getUserCertForUkey();
    }
    else if(lastCertid=="YQDKEY"){
        getUserCertForYQDId();
    }


}
function call_back2(data){
    userCert = data.retVal;
    //alert("用户证书："+userCert);
    getbtnonsubmit() ;
}
function call_back3(data){
    strCertID = data.retVal;
    //alert(userCertId);
    getUserCertForYQD(strCertID);
}

function call_back4(data){

    var handler = new HttpHandler("bp.jianyu.stamp.AnyWriteClientTool");
    handler.AddFormData();
    handler.AddPara("sealNum", data.retVal);
    handler.AddPara("FK_Node", nodeID);
    handler.AddPara("stampType", stampType);
    //window.opener.document.form.name.value

    var data = handler.DoMethodReturnString("directSignBatch");

    alert(data);


    //alert("身份证："+data.retVal);
    //SOF_GenRandom(10,call_back5);//获取随机数(非必要)

}

//随机数签名
function call_back5(data){
    alert("随机数："+data.retVal);
    SOF_SignData(strCertID,data.retVal,call_back6);//随机数签名
}
function call_back6(data){
    alert(data.retVal);
}



//登录
function btnonsubmit(stamptype) {

    strCertID =  LoginForm.UserList.value;

    var strPin = LoginForm.UserPwd.value;

    //登录
    SOF_Login(strCertID,strPin,call_back);

}
//判断使用usbkey登录还是易签盾登录
function ukeyoryqd() {
    GetDeviceInfo(strCertID,16,call_back1);

}
//获取用户证书--UKEY
function getUserCertForUkey() {
    //alert("getUserCert");
    //获取用户证书
    SOF_ExportUserCert(strCertID,call_back2);

}

//获取易签盾用户证书ID--YQDKEY
function getUserCertForYQDId() {
    //获取易签盾用户证书id
    SOF_GetLastLoginCertID(call_back3);
    //SOF_ExportUserCert(SOF_GetLastLoginCertID(),call_back2);
}
//获取易签盾用户证书--YQDKEY
function getUserCertForYQD(strCertID) {
    //获取易签盾用户证书
    SOF_ExportUserCert(strCertID,call_back2);
}

//获取用户身份证
function getbtnonsubmit() {

    //SOF_GetCertInfoByOid(userCert,'1.2.86.11.7.1',call_back3);
    SOF_GetCertInfoByOid(userCert,'1.2.156.112562.2.1.1.2',call_back4);

}