<template>
    <div class="allHeight">
      <Spin :spinning="loading" class="allHeight">
        <Table :columns="column" :data-source="dataSource" :defaultExpandAllRows="true">
          <template #headerCell="{ column, record }">
            <template v-if="column.dataIndex == 'action'">
              <div style="text-align: center;">
                操作
              </div>
            </template>
          </template>
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex == 'Port'">
              <div v-if="record.Port" style="color:red;">
               {{record.Port}}
              </div>
            </template>
            <template v-if="column.dataIndex == 'action'">
              <div v-if="record.Interface" style="display: flex;width: 100%;justify-content: space-evenly;">
                <Button type="primary" size="small" @click="doCarryOut(record)">执行接口</Button>
              </div>
            </template>
          </template>
        </Table>
        <Modal v-model:visible="dataModal.visible" :title="dataModal.title" :footer="null" :width="dataModal.modalWidth"  :body-style="dataModal.modalHeight">
            <div style="overflow: hidden auto;height: calc(100%);">
                <pre> {{ dataModal.jsonData }} </pre>
            </div>
        </Modal>
      </Spin>
    </div>
  </template>
  <script lang="ts" setup>
  import { useUserStore } from '@/stores/user';
  import { Table, Spin, Button, Modal } from 'ant-design-vue';
  import type { ColumnType } from 'ant-design-vue/lib/table';
  import { reactive, ref } from 'vue';
  import  apiList  from '@/portal/apiList';

  const loading = ref(false);
  const dataSource = ref<any>([]);
  const userStore = useUserStore();
  // const host = import.meta.env.VITE_GLOB_VUE3_URL   //获取vue3Host
  // const Token = userStore.getToken;
  const dataModal = reactive<Recordable>({
    title:'',
    visible:false,
    jsonData:{},
    modalWidth:800,
    modalHeight: {
        height: '500px',
        },
  })
  const column: ColumnType[] = [
    {
      title: '接口名称',
      dataIndex: 'Name',
      key: 'Name',
      fixed: 'left',
    }, {
      title: '说明',
      dataIndex: 'Docs',
      key: 'Docs',
      fixed: 'left',
    },{
      title: '接口',
      dataIndex: 'Port',
      key: 'Port',
      fixed: 'left',
    },{
      title: '操作',
      dataIndex: 'action',
      key: 'action',
      fixed: 'right',
      width: 200,
    }
  ];
  const InitPage = () => {
    dataSource.value = apiList;
  };

  /**
   * 执行
   * 执行接口,并获取json数据，通过弹窗弹出.
   * @param res 执行数据
   */
  const doCarryOut = async (row:Recordable) =>{
    let url 
    // window.open(copyData);
    switch (row.Name) {
        case '登录信息':
        //params: LoginParams , userNo: string, orgNo: string
        row.Interface({
            username: userStore.webUser.No,
            password: '123'
            },userStore.webUser.No,'').then((res: any)=>{
                dataModal.title = row.Name;
                dataModal.visible = true;
                dataModal.jsonData = res;
                console.log(res);
            })
            break;
        case '发起':
        case '待办':
        case '在途':
        case '抄送':
        case '近期':
        case '已完成':
        row.Interface().then((res: any)=>{
                console.log(res);
                dataModal.title = row.Name;
                dataModal.visible = true;
                dataModal.jsonData = res
            })
            break;
        case '创建WorkID(流程实例ID)':
        //Tookit演示流程  078
        //测试流程编号
        //flowNo: string, paras = ''
        row.Interface('108').then((res: any)=>{
                console.log(res);
                dataModal.title = row.Name;
                dataModal.visible = true;
                dataModal.jsonData = res
            })
            break;
        case '节点保存':
        //测试数据  workID: 702
        // params
            const obj = {
                token: userStore.getToken,
                workID:   908,
                keyVals: '@XingMing=张三@Age=18',
                dtlJSON: "{\"ND13701GeRenXinXi\":[{\"HuJi\":\"山东济南\",\"JiaTingZhuZhi\":\"菏泽\",}]}",
                athJSON: "{\"AthGeRenZhaoPian\":[{\"FileName\":\"006.jpg\",\"FileUrl\":\"http://222.190.125.90:8083/zcgl/sys/common/view/2025-01-20/jpg/22b9d1d9-57cc-4f82-8285-d557f442a209/006.jpg\",}]}",
            };
            const jsonData = JSON.parse(JSON.stringify(obj));
            console.log('jsonData',jsonData);
            row.Interface(jsonData).then((res: any)=>{
                    console.log(res);
                    dataModal.title = row.Name;
                    dataModal.visible = true;
                    dataModal.jsonData = res
                })
            break;
        case '节点发送':
            //测试发送数据  '078', 702,'7802','admin,'
            //flowNo: string, workID: number, toNodeID = 0, toEmps = ''
            row.Interface('108',   908 ,'10802','admin,').then((res: any)=>{
                    console.log(res);
                    dataModal.title = row.Name;
                    dataModal.visible = true;
                    dataModal.jsonData = res
                })
            break;
        case '节点退回':
            //测试退回 702,7802,'退回成功'
            //workID: number, toNodeID: number, msg: string
            row.Interface( 908 ,10802,'退回成功').then((res: any)=>{
                        console.log(res);
                        dataModal.title = row.Name;
                        dataModal.visible = true;
                        dataModal.jsonData = res
                    })
            break;
        case '节点移交':
            // 702 'yuwen','移交成功'
            //workID: number, toEmps: string, msg: string
            row.Interface( 908 ,'yuwen','移交成功').then((res: any)=>{
                        console.log(res);
                        dataModal.title = row.Name;
                        dataModal.visible = true;
                        dataModal.jsonData = res
                    })
            break;
         case '流程删除':
            // 702
            //workIDs: string
            row.Interface('908').then((res: any)=>{
                        console.log(res);
                         dataModal.title = row.Name;
                        dataModal.visible = true;
                        dataModal.jsonData = res
                    })
            break;
          case '单流程发起':
            // 108
            //flowNo: number, paras = ''
            url = row.Interface('108','&XingMing=王五&Age=23');
            console.log('url',url);
            dataModal.title = row.Name;
            dataModal.visible = true;
            dataModal.jsonData = url
            break;
          case '单流程查看':
            // 702
            //workID: number
            url = row.Interface(908);
            console.log('url',url);
            dataModal.title = row.Name;
            dataModal.visible = true;
            dataModal.jsonData = url
            break;
          case '单流程抄送':
            // '078',702
            //flowNo: number, workID : number
            url = row.Interface('108',908)
            console.log('url',url);
            dataModal.title = row.Name;
            dataModal.visible = true;
            dataModal.jsonData = url
            break;
          case '设计单个流程':
            // '078',702
            //flowNo: number, workID : number
            url = row.Interface('108')
            dataModal.title = row.Name;
            dataModal.visible = true;
            dataModal.jsonData = url
            break;
          case '设计单个表单':
            // '078',702
            //flowNo: number, workID : number
            url = row.Interface('ND10801')
            dataModal.title = row.Name;
            dataModal.visible = true;
            dataModal.jsonData = url
            break;
          case '轨迹':
            // '078',702
            //flowNo: number, workID : number
            url = row.Interface('108',908)
            console.log('url',url);
            dataModal.title = row.Name;
            dataModal.visible = true;
            dataModal.jsonData = url
            break;
    }
  }
  InitPage();
  
  </script>
  <style lang="less" scoped>
  .ant-spin-nested-loading {
    max-height: 100%;
  
    .ant-spin-container {
      max-height: 100%;
    }
  }
  
  .allHeight {
    height: 100%;
  }
  
  .total {
    height: 40px;
    margin-top: 20px;
  }
  
  .btn_Back {
    float: right;
  }
  </style>