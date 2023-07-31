import api from '../base'

const fetchRoom = {
  
  create : async ({roomName, user_id}) =>{
      return await api({
        method : 'POST',
        url : `/api/receiveDB/chat_room`,
        data:{
          name : roomName,
          user_id
        }
      })
      .catch(error =>{
        if (error.response){
          // console.log(error.response.data)
          // console.log(error.response.status)
          // console.log(error.response.headers)
        }else if(error.request){
          // console.log(error.request)
        }else{
          // console.log('Error', error.message)
        }
      })
  },
  
  read : async () =>{
    return await api({
      method : 'GET',
      url : `/api/receiveDB/chat_room`,
    })
    .catch(error =>{
      if (error.response){
        // console.log(error.response.data)
        // console.log(error.response.status)
        // console.log(error.response.headers)
      }else if(error.request){
        // console.log(error.request)
      }else{
        // console.log('Error', error.message)
      }
    })
  },

  join : async({roomName, email}) =>{
    
    return await api({
      method : 'POST',
      url : `/api/receiveDB/room_member`,
      data:{
        name : roomName,
        email
        // 유저 정보 넣어야함...
      }
    })
    .catch(error =>{
      if (error.response){
        // console.log(error.response.data)
        // console.log(error.response.status)
        // console.log(error.response.headers)
      }else if(error.request){
        // console.log(error.request)
      }else{
        // console.log('Error', error.message)
      }
    })
  },
  execute : async() =>{
    return await api({
      method : 'DELETE',
      url : `/api/receiveDB/room_member`
    })
    .catch(error =>{
      if (error.response){
        // console.log(error.response.data)
        // console.log(error.response.status)
        // console.log(error.response.headers)
      }else if(error.request){
        // console.log(error.request)
      }else{
        // console.log('Error', error.message)
      }
    })
  }
}

export {
  fetchRoom
} 