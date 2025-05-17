using System;

namespace BP.En30.NetCore.Tools
{
    public class SnowflakeIdGenerator
    {
        private readonly long _dataCenterId;
        private readonly long _machineId;
        private long _sequence = 0L;

        private const long Twepoch = 1288834974657L;
        private const long DataCenterIdBits = 5L;
        private const long MachineIdBits = 5L;
        private const long SequenceBits = 12L;

        private readonly long _maxDataCenterId = -1L ^ (-1L << (int)DataCenterIdBits);
        private readonly long _maxMachineId = -1L ^ (-1L << (int)MachineIdBits);
        private readonly long _sequenceMask = -1L ^ (-1L << (int)SequenceBits);

        private long _lastTimestamp = -1L;

        public SnowflakeIdGenerator(long dataCenterId, long machineId)
        {
            if (dataCenterId > _maxDataCenterId || dataCenterId < 0)
                throw new ArgumentException($"Data Center Id can't be greater than {_maxDataCenterId} or less than 0.");
            if (machineId > _maxMachineId || machineId < 0)
                throw new ArgumentException($"Machine Id can't be greater than {_maxMachineId} or less than 0.");

            _dataCenterId = dataCenterId;
            _machineId = machineId;
        }

        public long NextId()
        {
            lock (this) // Ensure thread-safety
            {
                long timestamp = GetCurrentMilliseconds();

                if (timestamp < _lastTimestamp)
                {
                    throw new Exception($"Clock moved backwards. Refusing to generate id for {(_lastTimestamp - timestamp)} milliseconds.");
                }

                if (_lastTimestamp == timestamp)
                {
                    _sequence = (_sequence + 1) & _sequenceMask;
                    if (_sequence == 0)
                    {
                        timestamp = WaitNextMillis(_lastTimestamp);
                    }
                }
                else
                {
                    _sequence = 0L;
                }

                _lastTimestamp = timestamp;

                return ((timestamp - Twepoch) << (int)(DataCenterIdBits + MachineIdBits + SequenceBits))
                       | (_dataCenterId << (int)(MachineIdBits + SequenceBits))
                       | (_machineId << (int)SequenceBits)
                       | _sequence;
            }
        }

        private long WaitNextMillis(long lastTimestamp)
        {
            long timestamp = GetCurrentMilliseconds();
            while (timestamp <= lastTimestamp)
            {
                timestamp = GetCurrentMilliseconds();
            }
            return timestamp;
        }

        private long GetCurrentMilliseconds()
        {
            return DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
        }
    }

}
