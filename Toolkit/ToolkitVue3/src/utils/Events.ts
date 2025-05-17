import mitt, { type Emitter } from 'mitt';

type GloEvents = {
  'turn-to-demo': any;
  'api-demo': any;
};

const emitter: Emitter<GloEvents> = mitt();
export default emitter;
