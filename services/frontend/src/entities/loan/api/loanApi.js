/**
 * 대여(loan) 엔티티 API
 */

/** S-410 대여 현황 목록 */
export const fetchLoanList = async (connector, { status, page, size } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/loan', {
    params: { status, page, size },
  });
  return response.data;
};

/** S-411 승인대기 목록 */
export const fetchPendingLoans = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/loan/pending');
  return response.data;
};

/** S-421 내 대여 자산 */
export const fetchMyLoans = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/loan/my');
  return response.data;
};

/** S-420 대여 가능 자산 - LOANABLE·ON_LOAN 전체 */
export const fetchAvailableLoanAssets = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/loan/available');
  return response.data;
};

/** S-412 대여 처리 (관리자 대행) */
export const createLoanByAdmin = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/loan', body);
  return response.data;
};

/** S-420 QR 스캔 대여 (임직원 본인) */
export const createLoanBySelf = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/loan/self', body);
  return response.data;
};

/** S-411 승인 */
export const approveLoan = async (connector, loanId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/loan/${loanId}/approve`);
  return response.data;
};

/** S-411 반려 */
export const rejectLoan = async (connector, loanId, reason) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/loan/${loanId}/reject`, { reason });
  return response.data;
};

/** S-421 반납 (임직원 본인) */
export const returnLoanBySelf = async (connector, loanId, abnormal) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/loan/${loanId}/self-return`, { abnormal });
  return response.data;
};

/** S-421 연장 (임직원 본인) */
export const extendLoanBySelf = async (connector, loanId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/loan/${loanId}/self-extend`);
  return response.data;
};
