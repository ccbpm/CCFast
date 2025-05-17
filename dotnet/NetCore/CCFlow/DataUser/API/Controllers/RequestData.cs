namespace CCFlow.NetCore.DataUser.API.Controllers
{
    public class RequestData
    {
        public string token { get; set; }
        public string frmID { get; set; }
        public string pkValue { get; set; }
        public string data { get; set; }
    }
    public class RequestMainTableAndDtlTable : RequestData
    {
        public string dataNew { get; set; }
        public string dataOld { get; set; }

    }
}
