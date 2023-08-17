import { 
    Create as createContract,
    Read_Producer as readContract_P,
    Read_Consumer as readContract_C,
    Update as updateContract
} from '@/models/contract/contract'
import { readQuery } from '@/db/mysql/query/crud'
import { buildConditionQuery } from '@/lib/queryBuilder';

type dataForm = {
    id : BigInt,
    content : Text,
    author : String, 
    score : Number,
};

import withCors from '../cors'

const receiveData = withCors(async (req: any, res: any) => {
    
    if (req.method === 'GET') {
    // 나머지 GET 로직...
    //   const inputData = req.query;
        const {id} = req.query;
        console.log(req.query);
        const {conditionQuery, values} = buildConditionQuery({id}, ' AND ');
        // const result = await readQuery('history', {conditionQuery, values});
        console.log({conditionQuery, values})
        const result = await readQuery('History', {conditionQuery, values});
        console.log(result)
        res.status(200).json({ contract_data : result, message : "Send Contract Data "})
        return;
    }

    if (req.method === 'POST') {

        const {contractId} = req.body;   
          // const contract = await uploadToS3('contract', image_consumer.originalname, image_consumer.buffer);
        const contract_id = contractId;

        //   const {conditionQuery, values} = buildConditionQuery(id, ' AND ');
          
        //   const NewConstractData = { payed : 1 } // -1 : 대여 중
          
          const result = await updateContract({ payed : 1 }, contract_id);

          // const contractData = await readQuery('history',{conditionQuery, values})
          if (!result){
            res.status(500).json({ message: 'contract failed cuz of server error' });
            return;
          }
          // 이미지 저장하는거 추가해야함
          res.status(200).json({ payed : 1, message: 'contract finished' });
          return
        }
        

    return res.status(500).json({ error: 'cannot catch methods...' });
});

export default receiveData;