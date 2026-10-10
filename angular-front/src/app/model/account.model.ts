export interface Account{
  id? :string,
  balance? : number,
  createdAt? :string,
  type? :string,
  customerId? :number
}
export enum RequestStatus{
  ERROR, SUCCESS
}
export interface AccountListState {
  accounts? : Account[],
  status? : RequestStatus
  errorMessage? : string
}
