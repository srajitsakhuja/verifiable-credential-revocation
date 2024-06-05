import * as Name from 'w3name'
import * as uint8arrays from 'uint8arrays';


// Uncomment the following block to create a new IPNS name
const ipsName = await Name.create()
console.log('Name:', ipsName.toString())
console.log('key: ' + uint8arrays.toString(ipsName.key.bytes, 'base64pad'))
const value = '/ipfs/QmfRbtDe6ZERJZjDE4H1bPnhmxZtmNsfanCJCoLHw91XDV'
const revision = await Name.v0(ipsName, value)
//
await Name.publish(revision, ipsName.key)


// Uncomment the following block to update an existing IPNS name
 // const ipnsSigningKey = "CAESQCJjPwuBgqPrYOyxZ2vb9dZ+zq4I+Jnw/aULYMgG4Ndc7zWOcUJmyuLz2wqmWcFaDjY7uE3VhBIAQqFPILksWJ0=";
 // const ipsName = await Name.from(uint8arrays.fromString(ipnsSigningKey, 'base64pad'));
 //console.log(ipsName.toString())
// let revision = await Name.resolve(ipsName)
// const value = 'foobar'
// const nextRevision = await Name.increment(revision, value)
// await Name.publish(nextRevision, ipsName.key)