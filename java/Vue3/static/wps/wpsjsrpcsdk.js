(function (global, factory) {
  'use strict';

  if (typeof module === 'object' && typeof module.exports === 'object') {
    module.exports = factory(global, true);
  } else {
    factory(global);
  }
})(typeof window !== 'undefined' ? window : this, function (window, noGlobal) {
  'use strict';

  var bFinished = true;

  /**
   * 兼容IE低版本的创建httpobj对象的方法
   * @returns httpobj，可用于进行数据传输的http的对象
   */
  function getHttpObj() {
    var httpobj = null;
    if (IEVersion() < 10) {
      try {
        httpobj = new XDomainExtRequest();
      } catch (e1) {
        httpobj = new createXHR();
      }
    } else {
      httpobj = new createXHR();
    }
    return httpobj;
  }
  //兼容IE低版本的创建xmlhttprequest对象的方法
  /**
   * 兼容IE低版本的创建xmlhttprequest对象的方法
   * @returns xmlhttprequest对象，兼容低版本IE
   */
  function createXHR() {
    if (typeof XMLHttpRequest != 'undefined') {
      //兼容高版本浏览器
      return new XMLHttpRequest();
    } else if (typeof ActiveXObject != 'undefined') {
      //IE6 采用 ActiveXObject， 兼容IE6
      var versions = [
        //由于MSXML库有3个版本，因此都要考虑
        'MSXML2.XMLHttp.6.0',
        'MSXML2.XMLHttp.3.0',
        'MSXML2.XMLHttp',
      ];

      for (var i = 0; i < versions.length; i++) {
        try {
          return new ActiveXObject(versions[i]);
        } catch (e) {
          //跳过
        }
      }
    } else {
      throw new Error('您的浏览器不支持XHR对象');
    }
  }

  /**
   * 通过该接口给服务器发请求
   * @param {*} options       参数对象，具体包含的参数如下：
   * @param {*} url           网页路径，传输地址
   * @param {*} type          传输类型，POST / GET / PUT
   * @param {*} sendData      传输的数据
   * @param {*} callback      回调函数
   * @param {*} tryCount      请求失败后的尝试次数
   * @param {*} bPop          是否弹出浏览器提示对话框
   * @param {*} timeout       请求等待响应的时间，超过时间请求实效
   * @param {*} concurrent    请求是否同步发送
   * @param {*} wpsclient        wpsclient对象
   * @returns NULL
   */
  function startWps(options) {
    if (!bFinished && !options.concurrent) {
      if (options.callback)
        options.callback({
          status: 1,
          message: '上一次请求没有完成',
        });
      return;
    }
    bFinished = false;

    function startWpsInnder(tryCount) {
      if (tryCount <= 0) {
        if (bFinished) return;
        bFinished = true;
        if (options.callback)
          options.callback({
            status: 2,
            message: '请允许浏览器打开WPS Office',
          });
        return;
      }
      var xmlReq = getHttpObj();
      //WPS客户端提供的接收参数的本地服务，HTTP服务端口为58890，HTTPS服务端口为58891
      //这俩配置，取一即可，不可同时启用
      xmlReq.open('POST', options.url);
      xmlReq.onload = function (res) {
        bFinished = true;
        if (options.callback) {
          options.callback({
            status: 0,
            response: IEVersion() < 10 ? xmlReq.responseText : res.target.response,
          });
        }
      };
      xmlReq.ontimeout = xmlReq.onerror = function (res) {
        xmlReq.bTimeout = true;
        if (tryCount == options.tryCount && options.bPop) {
          //打开wps并传参
          window.location.href = 'ksoWPSCloudSvr://start=RelayHttpServer'; //是否启动wps弹框
        }
        setTimeout(function () {
          startWpsInnder(tryCount - 1);
        }, 1000);
      };
      if (IEVersion() < 10) {
        xmlReq.onreadystatechange = function () {
          if (xmlReq.readyState != 4) return;
          if (xmlReq.bTimeout) {
            return;
          }
          if (xmlReq.status === 200) xmlReq.onload();
          else xmlReq.onerror();
        };
      }
      xmlReq.timeout = options.timeout;
      xmlReq.send(options.sendData);
    }
    startWpsInnder(options.tryCount);
    return;
  }

  var fromCharCode = String.fromCharCode;
  // encoder stuff
  var cb_utob = function (c) {
    if (c.length < 2) {
      var cc = c.charCodeAt(0);
      return cc < 0x80
        ? c
        : cc < 0x800
        ? fromCharCode(0xc0 | (cc >>> 6)) + fromCharCode(0x80 | (cc & 0x3f))
        : fromCharCode(0xe0 | ((cc >>> 12) & 0x0f)) + fromCharCode(0x80 | ((cc >>> 6) & 0x3f)) + fromCharCode(0x80 | (cc & 0x3f));
    } else {
      var cc = 0x10000 + (c.charCodeAt(0) - 0xd800) * 0x400 + (c.charCodeAt(1) - 0xdc00);
      return fromCharCode(0xf0 | ((cc >>> 18) & 0x07)) + fromCharCode(0x80 | ((cc >>> 12) & 0x3f)) + fromCharCode(0x80 | ((cc >>> 6) & 0x3f)) + fromCharCode(0x80 | (cc & 0x3f));
    }
  };
  var re_utob = /[\uD800-\uDBFF][\uDC00-\uDFFFF]|[^\x00-\x7F]/g;
  var utob = function (u) {
    return u.replace(re_utob, cb_utob);
  };
  var _encode = function (u) {
    var isUint8Array = Object.prototype.toString.call(u) === '[object Uint8Array]';
    if (isUint8Array) return u.toString('base64');
    else return btoa(utob(String(u)));
  };

  if (typeof window.btoa !== 'function') window.btoa = func_btoa;

  function func_btoa(input) {
    var str = String(input);
    var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
    for (
      // initialize result and counter
      var block, charCode, idx = 0, map = chars, output = '';
      // if the next str index does not exist:
      //   change the mapping table to "="
      //   check if d has no fractional digits
      str.charAt(idx | 0) || ((map = '='), idx % 1);
      // "8 - idx % 1 * 8" generates the sequence 2, 4, 6, 8
      output += map.charAt(63 & (block >> (8 - (idx % 1) * 8)))
    ) {
      charCode = str.charCodeAt((idx += 3 / 4));
      if (charCode > 0xff) {
        throw new InvalidCharacterError("'btoa' failed: The string to be encoded contains characters outside of the Latin1 range.");
      }
      block = (block << 8) | charCode;
    }
    return output;
  }

  var encode = function (u, urisafe) {
    return !urisafe
      ? _encode(u)
      : _encode(String(u))
          .replace(/[+\/]/g, function (m0) {
            return m0 == '+' ? '-' : '_';
          })
          .replace(/=/g, '');
  };

  /**
   * 获取IE浏览器版本
   * @returns     IE浏览器版本
   */
  function IEVersion() {
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    var isIE = userAgent.indexOf('compatible') > -1 && userAgent.indexOf('MSIE') > -1; //判断是否IE<11浏览器
    var isEdge = userAgent.indexOf('Edge') > -1 && !isIE; //判断是否IE的Edge浏览器
    var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf('rv:11.0') > -1;
    if (isIE) {
      var reIE = new RegExp('MSIE (\\d+\\.\\d+);');
      reIE.test(userAgent);
      var fIEVersion = parseFloat(RegExp['$1']);
      if (fIEVersion == 7) {
        return 7;
      } else if (fIEVersion == 8) {
        return 8;
      } else if (fIEVersion == 9) {
        return 9;
      } else if (fIEVersion == 10) {
        return 10;
      } else {
        return 6; //IE版本<=7
      }
    } else if (isEdge) {
      return 20; //edge
    } else if (isIE11) {
      return 11; //IE11
    } else {
      return 30; //不是ie浏览器
    }
  }

  /**
   * 启动wps客户端，加载项执行操作，无返回值
   * @param {*} options       参数对象，详情见下：
   * @param {*} clientType    加载项类型， wps / wpp / et
   * @param {*} name          加载项名称
   * @param {*} func          客户端加载项要执行的方法
   * @param {*} param         客户端家乡执行方法的参数
   * @param {*} urlBase       网页路径前缀
   * @param {*} callback      回调函数
   * @param {*} tryCount      请求失败后的尝试次数
   * @param {*} bPop          是否弹出浏览器提示对话框
   * @param {*} wpsclient     wpsclient对象
   */
  function WpsStart(options) {
    var startInfo = {
      name: options.name,
      function: options.func,
      info: options.param.param,
      jsPluginsXml: options.param.jsPluginsXml,
    };
    var strData = JSON.stringify(startInfo);
    if (IEVersion() < 10) {
      try {
        eval("strData = '" + JSON.stringify(startInfo) + "';");
      } catch (err) {}
    }

    var baseData = encode(strData);
    var url = options.urlBase + '/' + options.clientType + '/runParams';
    var data = 'ksowebstartup' + options.clientType + '://' + baseData;
    startWps({
      url: url,
      sendData: data,
      callback: options.callback,
      tryCount: options.tryCount,
      bPop: options.bPop,
      timeout: 5000,
      concurrent: false,
      client: options.wpsclient,
    });
  }

  /**
   * 服务端版本为空时，通过该接口启动wps
   * @param {*} options       参数对象，详情见下：
   * @param {*} clientType    加载项类型， wps / wpp / et
   * @param {*} name          加载项名称
   * @param {*} func          客户端加载项要执行的方法
   * @param {*} param         客户端家乡执行方法的参数
   * @param {*} urlBase       网页路径前缀
   * @param {*} callback      回调函数
   * @param {*} wpsclient     wpsclient对象
   * @param {*} concurrent    请求是否同步发送
   */
  function WpsStartWrap(options) {
    WpsStart({
      clientType: options.clientType,
      name: options.name,
      func: options.func,
      param: options.param,
      urlBase: options.urlBase,
      callback: options.callback,
      tryCount: 4,
      bPop: true,
      wpsclient: options.wpsclient,
    });
  }

  /**
   * 支持浏览器触发，WPS有返回值的启动
   *
   * @param {*} clientType	组件类型
   * @param {*} name			WPS加载项名称
   * @param {*} func			WPS加载项入口方法
   * @param {*} param			参数：包括WPS加载项内部定义的方法，参数等
   * @param {*} callback		回调函数
   * @param {*} tryCount		重试次数
   * @param {*} bPop			是否弹出浏览器提示对话框
   */
  var exId = 0;
  function WpsStartWrapExInner(options) {
    var infocontent = options.param.param;
    if (!options.wpsclient) {
      infocontent = JSON.stringify(options.param.param);
      var rspUrl = options.urlBase + '/transferEcho/runParams';
      var time = new Date();
      var cmdId = 'js' + time.getTime() + '_' + exId;
      var funcEx = 'var res = ' + options.func;
      var cbCode = "var xhr = new XMLHttpRequest();xhr.open('POST', '" + rspUrl + "');xhr.send(JSON.stringify({id: '" + cmdId + "', response: res}));"; //res 为func执行返回值
      var infoEx = infocontent + ');' + cbCode + 'void(0';
      options.func = funcEx;
      infocontent = infoEx;
    }
    var startInfo = {
      name: options.name,
      function: options.func,
      info: infocontent,
      showToFront: options.param.showToFront,
      jsPluginsXml: options.param.jsPluginsXml,
    };

    var strData = JSON.stringify(startInfo);
    if (IEVersion() < 10) {
      try {
        eval("strData = '" + JSON.stringify(startInfo) + "';");
      } catch (err) {}
    }

    var baseData = encode(strData);
    var wrapper;

    if (!options.wpsclient) {
      var url = options.urlBase + '/transfer/runParams';
      var data = 'ksowebstartup' + options.clientType + '://' + baseData;
      wrapper = {
        id: cmdId,
        app: options.clientType,
        data: data,
      };
    } else {
      var url = options.urlBase + '/transferEx/runParams';
      wrapper = {
        id: options.wpsclient.clientId,
        app: options.clientType,
        data: baseData,
        mode: options.wpsclient.silentMode ? 'true' : 'false',
      };
    }
    wrapper = JSON.stringify(wrapper);
    startWps({
      url: url,
      sendData: wrapper,
      callback: options.callback,
      tryCount: options.tryCount,
      bPop: options.bPop,
      timeout: 0,
      concurrent: options.concurrent,
      client: options.wpsclient,
    });
  }

  var serverVersion = 'wait';
  var cloudSvrStart = true;
  function WpsStartWrapVersionInner(options) {
    if (serverVersion == 'wait') {
      if (cloudSvrStart == false) {
        window.location.href = 'ksoWPSCloudSvr://start=RelayHttpServer'; //是否启动wps弹框
      }
      startWps({
        url: options.urlBase + '/version',
        data: '',
        callback: function (res) {
          if (res.status !== 0) {
            options.callback(res);
            return;
          }
          serverVersion = res.response;
          cloudSvrStart = true;
          options.tryCount = 1;
          options.bPop = false;
          if (serverVersion === '') {
            WpsStart(options);
          } else if (serverVersion < '1.0.1' && options.wpsclient) {
            if (options.callback) {
              options.callback({
                status: 4,
                message: '当前客户端不支持，请升级客户端',
              });
            }
          } else {
            WpsStartWrapExInner(options);
          }
        },
        tryCount: 4,
        bPop: true,
        timeout: 5000,
        concurrent: options.concurrent,
      });
    } else {
      if (serverVersion === '') {
        WpsStartWrap(options);
      } else if (serverVersion < '1.0.1' && options.wpsclient) {
        if (options.callback) {
          options.callback({
            status: 4,
            message: '当前客户端不支持，请升级客户端',
          });
        }
      } else {
        WpsStartWrapExInner(options);
      }
    }
  }

  var RegWebNotifyMap = { wps: {}, wpp: {}, et: {} };
  var bWebNotifyUseTimeout = true;
  function WebNotifyUseTimeout(value) {
    bWebNotifyUseTimeout = value ? true : false;
  }
  /**
   * 注册一个前端页面接收WPS传来消息的方法
   * @param {*} clientType wps | et | wpp
   * @param {*} name WPS加载项的名称
   * @param {*} callback 回调函数
   */
  function RegWebNotify(clientType, name, callback, wpsclient) {
    if (clientType != 'wps' && clientType != 'wpp' && clientType != 'et') return;
    var paramStr = {};
    if (wpsclient) {
      if (wpsclient.notifyRegsitered == true) {
        return;
      }
      wpsclient.notifyRegsitered = true;
      paramStr = {
        clientId: wpsclient.clientId,
        name: name,
        type: clientType,
      };
    } else {
      if (typeof callback != 'function') return;
      if (RegWebNotifyMap[clientType][name]) {
        RegWebNotifyMap[clientType][name] = callback;
        return;
      }
      var RegWebNotifyID = new Date().valueOf() + '';
      paramStr = {
        id: RegWebNotifyID,
        name: name,
        type: clientType,
      };
      RegWebNotifyMap[clientType][name] = callback;
    }

    var askItem = function () {
      var xhr = getHttpObj();
      xhr.onload = function (e) {
        if (xhr.responseText == 'WPSInnerMessage_quit') {
          return;
        }
        if (wpsclient) {
          wpsclient.OnRegWebNotify(xhr.responseText);
        } else {
          var func = RegWebNotifyMap[clientType][name];
          func(xhr.responseText);
        }
        window.setTimeout(askItem, 300);
      };
      xhr.onerror = function (e) {
        if (bWebNotifyUseTimeout) window.setTimeout(askItem, 1000);
        else window.setTimeout(askItem, 10000);
      };
      xhr.ontimeout = function (e) {
        if (bWebNotifyUseTimeout) window.setTimeout(askItem, 300);
        else window.setTimeout(askItem, 10000);
      };
      if (IEVersion() < 10) {
        xhr.onreadystatechange = function () {
          if (xhr.readyState != 4) return;
          if (xhr.bTimeout) {
            return;
          }
          if (xhr.status === 200) xhr.onload();
          else xhr.onerror();
        };
      }
      xhr.open('POST', GetUrlBase() + '/askwebnotify', true);
      if (bWebNotifyUseTimeout) xhr.timeout = 2000;
      xhr.send(JSON.stringify(paramStr));
    };

    window.setTimeout(askItem, 2000);
  }

  function GetUrlBase() {
    if (location.protocol == 'http:') return 'http://127.0.0.1:58890';
    return 'https://127.0.0.1:58891';
  }

  /**
   * 获取服务端版本号的接口，这里主要是初始化一些参数
   * @param {*} clientType    加载项类型， wps / wpp / et
   * @param {*} name          加载项名称
   * @param {*} func          客户端加载项要执行的方法
   * @param {*} param         客户端加载项执行方法的参数
   * @param {*} callback      回调函数
   * @param {*} showToFront   设置客户端是否显示到前面
   * @param {*} jsPluginsXml  设置加载项路径
   * @param {*} silentMode    是否是静默启动
   */
  function WpsStartWrapVersion(clientType, name, func, param, callback, showToFront, jsPluginsXml, silentMode) {
    var paramEx = {
      jsPluginsXml: jsPluginsXml ? jsPluginsXml : '',
      showToFront: typeof showToFront == 'boolean' ? showToFront : true,
      param: typeof param == 'object' ? param : JSON.parse(param),
    };
    var options = {
      clientType: clientType,
      name: name,
      func: func,
      param: paramEx,
      urlBase: GetUrlBase(),
      callback: callback,
      wpsclient: undefined,
      concurrent: true,
    };
    WpsStartWrapVersionInner(options);
  }

  //从外部浏览器远程调用 WPS 加载项中的方法
  var WpsInvoke = {
    InvokeAsHttp: WpsStartWrapVersion,
    InvokeAsHttps: WpsStartWrapVersion,
    RegWebNotify: RegWebNotify,
    ClientType: {
      wps: 'wps',
      et: 'et',
      wpp: 'wpp',
    },
    CreateXHR: getHttpObj,
    IsClientRunning: IsClientRunning,
  };

  window.wpsclients = [];
  /**
   * @constructor WpsClient           wps客户端
   * @param {string} clientType       必传参数，加载项类型，有效值为"wps","wpp","et"；分别表示文字，演示，电子表格
   */
  function WpsClient(clientType) {
    /**
     * 设置RegWebNotify的回调函数，加载项给业务端发消息通过该函数
     * @memberof WpsClient
     * @member onMessage
     */
    this.onMessage;

    /**
     * 设置加载项路径
     * @memberof WpsClient
     * @member jsPluginsXml
     */
    this.jsPluginsXml;

    /**
     * 内部成员，外部无需调用
     */
    this.notifyRegsitered = false;
    this.clientId = '';
    this.concurrent = false;
    this.clientType = clientType;
    this.firstRequest = true;

    /**
     * 内部函数，外部无需调用
     * @param {*} options
     */
    this.initWpsClient = function (options) {
      options.clientType = this.clientType;
      options.wpsclient = this;
      options.concurrent = this.firstRequest ? true : this.concurrent;
      this.firstRequest = false;
      WpsStartWrapVersionInner(options);
    };

    /**
     * 以http启动
     * @param {string} name              加载项名称
     * @param {string} func              要调用的加载项中的函数行
     * @param {string} param             在加载项中执行函数func要传递的数据
     * @param {function({int, string})} callback        回调函数，status = 0 表示成功，失败请查看message信息
     * @param {bool} showToFront         设置wps是否显示到前面来
     * @return {string}                  "Failed to send message to WPS." 发送消息失败，客户端已关闭；
     *                                   "WPS Addon is not response." 加载项阻塞，函数执行失败
     */
    this.InvokeAsHttp = function (name, func, param, callback, showToFront) {
      function clientCallback(res) {
        //this不是WpsClient
        if (res.status !== 0 || serverVersion < '1.0.1') {
          if (callback) callback(res);
          return;
        }
        var resObject = JSON.parse(res.response);
        if (this.client.clientId == '') {
          this.client.clientId = resObject.clientId;
        }
        this.client.concurrent = true;
        if (typeof resObject.data == 'object') res.response = JSON.stringify(resObject.data);
        else res.response = resObject.data;
        if (IEVersion() < 10)
          // eval(" res.response = '" + res.response + "';"); // 我们的项目只支持ie10以上,所以这句代码基本用不到
          res.response = res.response;
        if (callback) callback(res);
        this.client.RegWebNotify(name);
      }
      var paramEx = {
        jsPluginsXml: this.jsPluginsXml ? this.jsPluginsXml : '',
        showToFront: typeof showToFront == 'boolean' ? showToFront : true,
        param: typeof param == 'object' ? param : JSON.parse(param),
      };
      this.initWpsClient({
        name: name,
        func: func,
        param: paramEx,
        urlBase: GetUrlBase(),
        callback: clientCallback,
      });
    };

    /**
     * 以https启动
     * @param {string} name              加载项名称
     * @param {string} func              要调用的加载项中的函数行
     * @param {string} param             在加载项中执行函数func要传递的数据
     * @param {function({int, string})} callback        回调函数，status = 0 表示成功，失败请查看message信息
     * @param {bool} showToFront         设置wps是否显示到前面来
     */
    this.InvokeAsHttps = function (name, func, param, callback, showToFront) {
      var paramEx = {
        jsPluginsXml: this.jsPluginsXml ? this.jsPluginsXml : '',
        showToFront: typeof showToFront == 'boolean' ? showToFront : true,
        param: typeof param == 'object' ? param : JSON.parse(param),
      };
      this.initWpsClient({
        name: name,
        func: func,
        param: paramEx,
        urlBase: GetUrlBase(),
        callback: callback,
      });
    };

    /**
     * 内部函数，外部无需调用
     * @param {*} name
     */
    this.RegWebNotify = function (name) {
      RegWebNotify(this.clientType, name, null, this);
    };

    /**
     * 消息注册函数的回调函数
     * @param {*} message   客户端发来的消息
     */
    this.OnRegWebNotify = function (message) {
      if (this.onMessage) this.onMessage(message);
    };

    /**
     * 以静默模式启动客户端
     * @param {string} name                 必传参数，加载项名称
     * @param {function({int, string})} [callback]         回调函数，status = 0 表示成功，失败请查看message信息
     */
    this.StartWpsInSilentMode = function (name, callback) {
      function initCallback(res) {
        //this不是WpsClient
        if (res.status !== 0 || serverVersion < '1.0.1') {
          if (callback) callback(res);
          return;
        }
        if (this.client.clientId == '') {
          this.client.clientId = JSON.parse(res.response).clientId;
          window.wpsclients[window.wpsclients.length] = { name: name, client: this.client };
        }
        res.response = JSON.stringify(JSON.parse(res.response).data);
        this.client.concurrent = true;
        if (callback) {
          callback(res);
        }
        this.client.RegWebNotify(name);
      }
      var paramEx = {
        jsPluginsXml: this.jsPluginsXml,
        showToFront: false,
        param: { status: 'InitInSilentMode' },
      };
      this.silentMode = true;
      this.initWpsClient({
        name: name,
        func: '',
        param: paramEx,
        urlBase: GetUrlBase(),
        callback: initCallback,
      });
    };

    /**
     * 显示客户端到最前面
     * @param {string} name             必传参数，加载项名称
     * @param {function({int, string})} [callback]     回调函数
     */
    this.ShowToFront = function (name, callback) {
      if (serverVersion < '1.0.1') {
        if (callback) {
          callback({
            status: 4,
            message: '当前客户端不支持，请升级客户端',
          });
          return;
        }
        return;
      }
      if (this.clientId == '') {
        if (callback)
          callback({
            status: 3,
            message: '没有静默启动客户端',
          });
        return;
      }
      var paramEx = {
        jsPluginsXml: '',
        showToFront: true,
        param: { status: 'ShowToFront' },
      };
      this.initWpsClient({
        name: name,
        func: '',
        param: paramEx,
        urlBase: GetUrlBase(),
        callback: callback,
      });
    };

    /**
     * 关闭未显示出来的静默启动客户端
     * @param {string} name             必传参数，加载项名称
     * @param {function({int, string})} [callback]     回调函数
     */
    this.CloseSilentClient = function (name, callback) {
      if (serverVersion < '1.0.1') {
        if (callback) {
          callback({
            status: 4,
            message: '当前客户端不支持，请升级客户端',
          });
          return;
        }
        return;
      }
      if (this.clientId == '') {
        if (callback)
          callback({
            status: 3,
            message: '没有静默启动客户端',
          });
        return;
      }
      var paramEx = {
        jsPluginsXml: '',
        showToFront: false,
        param: undefined,
      };
      var func;
      if (this.clientType == 'wps') func = 'wps.WpsApplication().Quit';
      else if (this.clientType == 'et') func = 'wps.EtApplication().Quit';
      else if (this.clientType == 'wpp') func = 'wps.WppApplication().Quit';

      function closeSilentClient(res) {
        if (res.status == 0) this.client.clientId = '';
        if (callback) callback(res);
        return;
      }
      this.initWpsClient({
        name: name,
        func: func,
        param: paramEx,
        urlBase: GetUrlBase(),
        callback: closeSilentClient,
      });
    };

    /**
     * 当前客户端是否在运行，使用WpsClient.IsClientRunning()进行调用
     * @param {function({int, string})} [callback]      回调函数，"Client is running." 客户端正在运行
     *                                                  "Client is not running." 客户端没有运行
     */
    this.IsClientRunning = function (callback) {
      if (serverVersion < '1.0.1') {
        if (callback) {
          callback({
            status: 4,
            message: '当前客户端不支持，请升级客户端',
          });
          return;
        }
        return;
      }
      IsClientRunning(this.clientType, callback, this);
    };
  }

  function InitSdk() {
    var url = GetUrlBase() + '/version';
    startWps({
      url: url,
      data: '',
      callback: function (res) {
        if (res.status !== 0) {
          cloudSvrStart = false;
          return;
        }
        if (serverVersion == 'wait') {
          serverVersion = res.response;
          cloudSvrStart = true;
        }
      },
      tryCount: 1,
      bPop: false,
      timeout: 5000,
    });
  }

  if (typeof noGlobal === 'undefined') {
    window.WpsInvoke = WpsInvoke;
    window.WpsClient = WpsClient;
    window.WebNotifyUseTimeout = WebNotifyUseTimeout;
    InitSdk();
  }

  /**
   * 当前客户端是否在运行，使用WpsInvoke.IsClientRunning()进行调用
   * @param {string} clientType       加载项类型
   * @param {function} [callback]      回调函数，"Client is running." 客户端正在运行
   *                                   "Client is not running." 客户端没有运行
   */
  function IsClientRunning(clientType, callback, wpsclient) {
    var url = GetUrlBase() + '/isRunning';
    var wrapper = {
      id: wpsclient == undefined ? undefined : wpsclient.clientId,
      app: clientType,
    };
    wrapper = JSON.stringify(wrapper);
    startWps({
      url: url,
      sendData: wrapper,
      callback: callback,
      tryCount: 1,
      bPop: false,
      timeout: 2000,
      concurrent: true,
      client: wpsclient,
    });
  }

  function WpsAddonGetAllConfig(callBack) {
    var baseData;
    startWps({
      url: GetUrlBase() + '/publishlist',
      type: 'GET',
      sendData: baseData,
      callback: callBack,
      tryCount: 3,
      bPop: true,
      timeout: 5000,
      concurrent: true,
    });
  }

  function WpsAddonVerifyStatus(element, callBack) {
    var xmlReq = getHttpObj();
    var offline = element.online === 'false';
    var url = offline ? element.url : element.url + 'ribbon.xml';
    xmlReq.open('POST', GetUrlBase() + '/redirect/runParams');
    xmlReq.onload = function (res) {
      if (offline && !res.target.response.startsWith('7z')) {
        callBack({ status: 1, msg: '不是有效的7z格式' + url });
      } else if (!offline && !res.target.response.startsWith('<customUI')) {
        callBack({ status: 1, msg: '不是有效的ribbon.xml, ' + url });
      } else {
        callBack({ status: 0, msg: 'OK' });
      }
    };
    xmlReq.onerror = function (res) {
      xmlReq.bTimeout = true;
      callBack({ status: 2, msg: '网页路径不可访问，如果是跨域问题，不影响使用' + url });
    };
    xmlReq.ontimeout = function (res) {
      xmlReq.bTimeout = true;
      callBack({ status: 3, msg: '访问超时' + url });
    };
    if (IEVersion() < 10) {
      xmlReq.onreadystatechange = function () {
        if (xmlReq.readyState != 4) return;
        if (xmlReq.bTimeout) {
          return;
        }
        if (xmlReq.status === 200) xmlReq.onload();
        else xmlReq.onerror();
      };
    }
    xmlReq.timeout = 5000;
    var data = {
      method: 'get',
      url: url,
      data: '',
    };
    var sendData = FormatSendData(data);
    xmlReq.send(sendData);
  }

  /**
   * 部署加载项，包括启动enable / disable禁用 / disableall禁用所有
   * @param {*} element   参数对象
   * @param {*} cmd       具体操作，enable / disable / disableall
   * @param {*} callBack  回调函数
   */
  function WpsAddonHandleEx(element, cmd, callBack) {
    var data = FormatData(element, cmd);
    startWps({
      url: GetUrlBase() + '/deployaddons/runParams',
      type: 'POST',
      sendData: data,
      callback: callBack,
      tryCount: 3,
      bPop: true,
      timeout: 0,
      concurrent: true,
    });
  }

  /**
   * 启用加载项
   * @param {*} element   参数对象
   * @param {*} callBack  回调函数
   */
  function WpsAddonEnable(element, callBack) {
    WpsAddonHandleEx(element, 'enable', callBack);
  }

  /**
   * 禁用加载项
   * @param {*} element   参数对象
   * @param {*} callBack  回调函数
   */
  function WpsAddonDisable(element, callBack) {
    WpsAddonHandleEx(element, 'disable', callBack);
  }

  /**
   * 禁用所有加载项
   * @param {*} element
   * @param {*} callBack
   */
  function WpsAddonDisableAll(element, callBack) {
    WpsAddonHandleEx(element, 'disableall', callBack);
  }

  /**
   * 生成json格式的数据
   * @param {*} element   参数对象
   * @param {*} cmd       具体操作，enable / disable / disableall
   * @returns base64编码后的数据
   */
  function FormatData(element, cmd) {
    var data = {
      cmd: cmd, //"enable", 启用， "disable", 禁用, "disableall", 禁用所有
      name: element.name,
      url: element.url,
      addonType: element.addonType,
      online: element.online,
      version: element.version,
    };
    return FormatSendData(data);
  }

  /**
   * 将json格式的数据字符串化，并进行base64编码
   * @param {*} data  数据
   * @returns base64编码后的数据
   */
  function FormatSendData(data) {
    var strData = JSON.stringify(data);
    if (IEVersion() < 10) eval("strData = '" + JSON.stringify(strData) + "';");
    return encode(strData);
  }

  window.onbeforeunload = function () {
    for (var i = 0; i < window.wpsclients.length; ++i) {
      var item = window.wpsclients[i];
      item.client.CloseSilentClient(item.name);
    }
  };

  //管理 WPS 加载项
  var WpsAddonMgr = {
    getAllConfig: WpsAddonGetAllConfig,
    verifyStatus: WpsAddonVerifyStatus,
    enable: WpsAddonEnable,
    disable: WpsAddonDisable,
    disableall: WpsAddonDisableAll,
  };

  if (typeof noGlobal === 'undefined') {
    window.WpsAddonMgr = WpsAddonMgr;
  }

  return { WpsInvoke: WpsInvoke, WpsAddonMgr: WpsAddonMgr, version: '1.0.12' };
});
