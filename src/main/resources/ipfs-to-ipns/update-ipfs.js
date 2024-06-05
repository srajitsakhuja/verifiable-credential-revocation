import * as Name from 'w3name'
import * as uint8arrays from 'uint8arrays';


// const ipsName = await Name.create()
// console.log('Name:', ipsName.toString())
// console.log('key: ' + uint8arrays.toString(ipsName.key.bytes, 'base64pad'))
// const value = '/ipfs/QmfRbtDe6ZERJZjDE4H1bPnhmxZtmNsfanCJCoLHw91XDV'
// const revision = await Name.v0(ipsName, value)
// //
// await Name.publish(revision, ipsName.key)

const ipnsSigningKey = process.argv[2]
//"CAESQNPlRh5LxWdAG/RRXAEYtaQJIu4SXD5QAAldnCs7NkTDcigoPKtNHbI+jWYti3StRYUPJNflxR8n8FtDU56ACVk=";
const ipsName = await Name.from(uint8arrays.fromString(ipnsSigningKey, 'base64pad'));
console.log(ipsName.toString())
let revision = await Name.resolve(ipsName)
const value = process.argv[3]
const nextRevision = await Name.increment(revision, value)
await Name.publish(nextRevision, ipsName.key)