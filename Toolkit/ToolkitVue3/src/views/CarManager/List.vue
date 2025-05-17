

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
                <div style="display: flex;width: 100%;justify-content: space-evenly;">
                  <!-- <Button type="primary" size="small" @click="doStart(record)">用车申请</Button> -->
                  <Button type="primary" size="small" @click="doHisRecord(record)">历史维修</Button>
                </div>
              </template>
            </template>
          </Table>
        </div>
        <div>
          <div class="controls"> 
            <ul>
              <div>-演示内容.</div>
              <li>1、本文件演示从一个实体台账上发起流程，并查看当前流程的历史数据.</li>
              <li>2、启动的流程</li>
              <div>- 车辆台账:</div>
              <li>1、流程启动: 只传入流程编号即可启动.  FlowNo=071</li>
              <li>2、查看历史维修流程时，跳转到流程二开页面 (SearchFlow) 查看该流程的历史记录.</li>
            </ul>
            <!-- <Button type="primary" size="small" class="car_btn" @click="doFlowSearch">历史维修数据</Button> -->
          </div>
        </div>
      </Spin>
    </div>
  </template>
  <script lang="ts" setup>
  import Dev2UrlInterface from '@/Toolkit/Dev2UrlInterface';
  import { Table, Spin, Button } from 'ant-design-vue';
  import type { ColumnType } from 'ant-design-vue/lib/table';
  import { ref } from 'vue';

  const loading = ref(false);
  const column: ColumnType[] = [
  {
      title: '车辆编号',
      dataIndex: 'PrjNo',
      fixed: 'left',
      width: 100,
      ellipsis: true,
    },
    {
      title: '车辆名称',
      dataIndex: 'PrjName',
      fixed: 'left',
      width: 200,
      ellipsis: true,
    },
    {
      title: '用车原因',
      dataIndex: 'YongCheYuanYin',
      fixed: 'left',
      width: 250,
      ellipsis: true,
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 200,
    }
  ];

  const dataSource = [
        {
            PrjNo:'100001',
            PrjName:'别克',
            YongCheYuanYin: '接送客户',
        },
        {
            PrjNo:'100002',
            PrjName:'吉利',
            YongCheYuanYin: '出差',
        },
     ];
  /**
   * 申请:跳转流程页面
   * @param res 行数据
   */
  const doStart = async (res:string) =>{
    const queryParams = new URLSearchParams(res).toString();
    // 072 车辆维修
    const url = await Dev2UrlInterface.Flow_MyFlowByFlowNo( '071', `&${queryParams}`);
    window.open(url);
  }

  /**
   * 历史维修：跳转到流程二开页面，通过PrjNo，PrjName区分
   * @param res 行数据
   */
  const doHisRecord = async(res: any) => {
    console.log('res', res)
    // 071 车辆台账
    const url = await Dev2UrlInterface.Flow_Search( '071');
    window.open(url);
  }

   /**
   * 打开流程二开页面
   * 
   */
   const doFlowSearch = async() => {
    // 071 车辆台账
    const url = await Dev2UrlInterface.Flow_Search( '071');
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
  
  .btn_Back {
    float: right;
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
  </style>

