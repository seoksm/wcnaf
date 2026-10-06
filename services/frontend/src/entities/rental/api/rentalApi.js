/**
 * 렌탈·구독(rental) 엔티티 API
 */

const DATE_ONLY_PATTERN = /^\d{4}-\d{2}-\d{2}$/;

// 렌탈 API의 자동 생성 DTO는 날짜 필드를 OffsetDateTime으로 받고,
// 서버 매퍼가 정오 UTC를 LocalDate로 변환한다. 날짜만 입력된 경우에만 그 계약에 맞춘다.
const toRentalApiDateTime = (value) => (
  typeof value === 'string' && DATE_ONLY_PATTERN.test(value) ? `${value}T12:00:00Z` : value
);

const withApiDateTimes = (body, dateFields) => {
  const converted = { ...body };
  dateFields.forEach((field) => {
    converted[field] = toRentalApiDateTime(body?.[field]);
  });
  return converted;
};

export const fetchRentals = async (connector, { keyword, page, size } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/rental-asset', { params: { keyword, page, size } });
  return response.data;
};

export const createRental = async (connector, body) => {
  const response = await connector.client.post(
    '/api/v1/smart-asset/rental-asset',
    withApiDateTimes(body, ['startDate', 'endDate']),
  );
  return response.data;
};

export const updateRental = async (connector, rentalAssetId, body) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/rental-asset/${rentalAssetId}`,
    withApiDateTimes(body, ['startDate', 'endDate']),
  );
  return response.data;
};

export const cancelRental = async (connector, rentalAssetId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/rental-asset/${rentalAssetId}/cancel`);
  return response.data;
};

export const fetchPaymentSchedules = async (connector, rentalAssetId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/rental-asset/${rentalAssetId}/payment-schedule`);
  return response.data;
};

export const addPaymentSchedule = async (connector, rentalAssetId, body) => {
  const response = await connector.client.post(
    `/api/v1/smart-asset/rental-asset/${rentalAssetId}/payment-schedule`,
    withApiDateTimes(body, ['accrualMonth', 'dueDate']),
  );
  return response.data;
};

export const updatePaymentSchedule = async (connector, paymentScheduleId, body) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/payment-schedule/${paymentScheduleId}`,
    withApiDateTimes(body, ['dueDate']),
  );
  return response.data;
};

export const confirmPaymentSchedule = async (connector, paymentScheduleId, actualAmount) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/payment-schedule/${paymentScheduleId}/confirm`, { actualAmount });
  return response.data;
};
