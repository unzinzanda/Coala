export default function setTimeStamp(datetime : any){
    var today = new Date(datetime);
    today.setHours(today.getHours() + 9);
    return today.toISOString().replace('T', ' ').substring(0, 19);
}