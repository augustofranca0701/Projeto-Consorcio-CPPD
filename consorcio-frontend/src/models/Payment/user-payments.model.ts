export interface UserPayments {
  id: number;
  dataVencimento: Date;
  valor: string;
  isPaid: boolean;
  nomeGrupo: string;
}
