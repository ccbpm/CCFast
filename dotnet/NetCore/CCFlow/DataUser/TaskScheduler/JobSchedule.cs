using System;

namespace CCFlow.NetCore.DataUser.TaskScheduler
{
    public class JobSchedule
    {
        public Type JobType {  get; }
        public string CronExp { get;  }

        public JobSchedule(Type jobType, string cronExp)
        {
            JobType = jobType;
            this.CronExp = cronExp;
        }

    }
}
