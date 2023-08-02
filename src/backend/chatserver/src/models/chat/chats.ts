import {
    createQuery,
    readQuery,
    updateQuery,
    deleteQuery
} from '@/db/mongo/query/crud'

import timestamp from '@/lib/timestamp';

import {
  buildSchema
} from '@/lib/queryBuilder'

type dataForm = {
  room_id : bigint,
  member_id : bigint, // 이후에 member_id 로 변경해야함
  text_content : Text,
};

const Create = async (inputData : dataForm) =>{
  try{
    const chatting_data = {...inputData, created_at : timestamp()}
    const result = await createQuery('chat_content', chatting_data);
    console.log("결과는",result)
    return result;
  }catch(error){
    console.log(error)
  }
}

const Read = async (chat_Log : object) => {
  try{
    const result = await readQuery('chat_content', chat_Log);
    return result;
  }catch(error){
    console.log(error)
  }
}


export { Create, Read }