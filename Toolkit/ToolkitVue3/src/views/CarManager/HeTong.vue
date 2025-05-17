<!-- 
     PrjNo, PrjName,JiaFang,YiFang,JE, HTSta, WorkIDOfSP 合同审批
-->
<template>
    <div class="allHeight">
      <Spin :spinning="loading">
        <div class="table_cont">
          <Table :columns="column" :data-source="dataSource" :pagination="false">
            <template #headerCell="{ column, record }">
              <template v-if="column.dataIndex == 'action'">
                <div style="text-align: center;">
                  操作
                </div>
              </template>
            </template>
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex == 'action'">
                <div  style="display: flex;width: 100%;justify-content: space-evenly;">
                  <Button v-if="record.HeTongSta==0" type="primary" size="small" @click="doStart(record)">启动流程</Button>
                  <Button v-else-if="record.HeTongSta==1" type="primary"  class="flow-deal" size="small" @click="doStart(record)">处理流程</Button>
                  <Button v-else-if="record.HeTongSta==2" type="primary" class="flow-inspect" size="small" @click="doStart(record)">检查流程</Button>
                </div>
              </template>
            </template>
          </Table>
        </div>
        <div>
          <div class="controls"> 
            <ul>
              <div>-演示内容.</div>
              <li>1、本文件演示从一个实体上发起流程,并在流程启动时把当前实体的主表数据通过api接口形式,传入流程的表单.</li>
              <li>2、启动的流程</li>
              <div>- 合同审批:</div>
              <li>1、流程启动: 通过WorkIDOfSP跟流程编号即可启动，如果WorkIDOfSP=0就启动新流程.  FlowNo=109</li>
              <li><div>2、启动方式:</div>
                <ol>判断HeTongSta: 
                  <li>当HeTongSta状态处于未审批或审批中时, 通过流程编号和WorkIDOfSP跳转到MyFlow页面;</li>
                  <li>当HeTongSta状态处于审批完毕时, 通过流程编号和WorkIDOfSP跳转到MyView页面.</li>
                </ol>
              </li>
            </ul>
          </div>
        </div>
      </Spin>
    </div>
  </template>
  <script lang="ts" setup>
  import Dev2ApiInterface from '@/Toolkit/Dev2ApiInterface';
  import { useUserStore } from '@/stores/user';
  import { Table, Spin, Button, message } from 'ant-design-vue';
  import type { ColumnType } from 'ant-design-vue/lib/table';
  import { reactive, ref } from 'vue';
  import { formatProject } from '@/utils/gener/ParamsUtils';
  import { QueryMerge } from '@/utils/gener/StringUtils';
  import Dev2UrlInterface from '@/Toolkit/Dev2UrlInterface';

  const loading = ref(false);
  const userStore = useUserStore();
  const Token = userStore.getToken;


  interface HtData {
    PrjNo: string,
    PrjName: string,
    party_a: string,
    party_b: string,
    amount: string,
    HeTongSta: number,
    HeTongStaName: string,
    WorkIDOfSP: string,
  }

  //Node_SaveWork保存内容
  const spliceObj = reactive<Recordable>({
    token: '',// 确保 Token 已经被定义和赋值
    keyVals: '',
    dtlJSON: '', // 确保 JSON 字符串正确闭合
    athJSON: '', // 确保 JSON 字符串正确闭合
  });
  const column: ColumnType[] = [
    {
      title: '合同编号',
      dataIndex: 'PrjNo',
      fixed: 'left',
      width: 150,
    },
    {
      title: '合同名称',
      dataIndex: 'PrjName',
      fixed: 'left',
      width: 200,
    },
    {
      title: '甲方',
      dataIndex: 'party_a',
      fixed: 'left',
      width: 200,
    },
    {
      title: '乙方',
      dataIndex: 'party_b',
      fixed: 'left',
      width: 200,
    },
    {
      title: '金额',
      dataIndex: 'amount',
      fixed: 'left',
      width: 150,
    },
    {
      title: '合同状态',
      dataIndex: 'HeTongStaName',
      fixed: 'left',
      width: 150,
    },
    {
      title: 'WorkIDOfSP',
      dataIndex: 'WorkIDOfSP',
      fixed: 'left',
      width: 100,
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 150,
    }
  ];
  //合同数据.
  const dataSource: HtData[] = [
    {
      PrjNo: '001',
      PrjName: '工作流采购',
      party_a: '驰骋公司',
      party_b: '堃用公司',
      amount: '100',
      HeTongSta: 0,
      HeTongStaName: '未审批',
      WorkIDOfSP: '0',
    },
    {
      PrjNo: '002',
      PrjName: '低代码采购',
      party_a: '驰骋公司',
      party_b: '广州铁路局',
      amount: '200',
      HeTongSta: 1,
      HeTongStaName: '审批中',
      WorkIDOfSP: '709',   //ke
    },
    {
      PrjNo: '003',
      PrjName: '表单引擎采购',
      party_a:'驰骋公司',
      party_b:'天宇公司',
      amount: '500',
      HeTongSta: 2,
      HeTongStaName: '审批完毕',
      WorkIDOfSP: '707',//ke
    },
  ];
  
  
  /**
    * 审批状态:审批中和审批完成的跳转MyView页面
   */
  const OpenMyView =async (row: any)=>{
    //1. 获取workid
    const workID= row.WorkIDOfSP;
    //2. 存储数据到流程中
    const rowJson = {
      ...row,
      'WorkIDOfSP':workID,
    }
    const jsonStr = formatProject(rowJson);
    spliceObj.token = Token,
    // spliceObj.workID = workID,
    spliceObj.keyVals = jsonStr;
    const jsonData = JSON.parse(JSON.stringify(spliceObj))
    await Dev2ApiInterface.Node_SaveWork(workID, jsonData).then((res:any) => {
        if(res.code === 200){
            message.info(res.message);  
        }
    });
    //3. 获取打开流程地址.
    const url = await Dev2UrlInterface.Flow_MyView(workID);
    window.open(url);
  }

  /**
    * 审批状态:发起的跳转MyFlow页面
   */
  const StartFlow =async (row:any)=>{
    //1. 首先创建workID. 
    //ke 测试 流程编号为077.
    const workID = await Dev2ApiInterface.Node_CreateBlank('077');
    console.log('workID',workID);
    //2. 把合同的数据保存到流程表单.
    //3. 把workID,保存到合同表中，让合同与流程产生对应. WorkIDOfSP.
    const rowJson = {
      ...row,
      'WorkIDOfSP':workID,
    }
    const jsonStr = formatProject(rowJson);
    spliceObj.token = Token,
    spliceObj.keyVals = jsonStr;
    const jsonData = JSON.parse(JSON.stringify(spliceObj))
    await Dev2ApiInterface.Node_SaveWork(workID, jsonData).then((res:any) => {
        if(res.code === 200){
            message.info(res.message);  
        }
    });

    //4. 把workID设置为草稿，锁定该workid.
    const workid = workID;
    await Dev2ApiInterface.Node_SetDraft(workid);

    //5. 获取打开流程地址.
    const url = await Dev2UrlInterface.Flow_MyFlowByFlowNo('077', `&WorkID=${workID}`);
    window.open(url);
  }

    /**
   * 启动:跳转页面
   * @param res 链接地址
   */
   const doStart = async (res:any) =>{
      try {
        console.log(res.HeTongSta);
        switch (res.HeTongSta) {
          case 0:
            StartFlow(res);
            break;
          case 1:
          case 2:
            OpenMyView(res);
            break;
        }
      }catch (e:any) {
        message.error(e.toString());
        console.trace(e.toString());
      }
  };

  /**
   * 跳转该流程
   */
  const skipUrl=async ()=>{
    const url = await Dev2UrlInterface.Flow_MyFlowByFlowNo( '077');
    window.open(url);
  }

  </script>
  <style lang="less" scoped>
  .ant-spin-nested-loading {
    max-height: 100%;
  
    .ant-spin-container {
      max-height: 100%;
    }
  }
  
  .allHeight {
    padding-top: 15px;
    height: 100%;
    background-color: #eeeeee;
  }
  
  .total {
    height: 40px;
    margin-top: 20px;
  }
  
  li{
    padding-top: 10px;
  }
  .controls{
    height: 55%;
    padding: 15px;
    margin: 0 15px;
    box-shadow: 1px 0px 5px 5px #eae4e4;
    background-color: #fff;
      .car_btn{
          margin-left: 10%;
        }
  }
  .table_cont{
    padding: 15px;
  }
  .btn_Back {
    float: right;
  }
  .flow-deal{
    border-color: #67c23a;
    background: #67c23a;
  }
  .flow-inspect{
    border-color: #fa8214;
    background: #fa8214;
  }
  </style>


