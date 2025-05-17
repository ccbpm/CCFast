using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BP.WF.Port.BaiDuOCR
{
    /// <summary>
    /// 百度地图返回数据
    /// </summary>
    public class BaiDuMap
    {
        public string address { get; set; }
        public MapContent content { get; set; }
    }
    //详细数据
    public class MapContent {
        public string address { get; set; }
        public MapPoint point { get; set; }
    }
    /// <summary>
    /// 坐标
    /// </summary>
    public class MapPoint {
        public string x { get; set; }
        public string y { get; set; }
    }
}
