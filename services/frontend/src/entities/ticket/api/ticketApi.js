/**
 * 서비스데스크 티켓(ticket) 엔티티 API
 */

const DATE_ONLY_PATTERN = /^\d{4}-\d{2}-\d{2}$/;

// 티켓 API의 자동 생성 DTO는 날짜 필드를 OffsetDateTime으로 받고,
// 서버 매퍼가 정오 UTC를 LocalDate로 변환한다. 날짜만 입력된 경우에만 그 계약에 맞춘다.
const toTicketApiDateTime = (value) => (
  typeof value === 'string' && DATE_ONLY_PATTERN.test(value) ? `${value}T12:00:00Z` : value
);

const withApiDateTimes = (body, dateFields) => {
  const converted = { ...body };
  dateFields.forEach((field) => {
    converted[field] = toTicketApiDateTime(body?.[field]);
  });
  return converted;
};

export const fetchTicketKanban = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/ticket/kanban');
  return response.data;
};

export const fetchTickets = async (connector, { keyword, status, page, size } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/ticket', { params: { keyword, status, page, size } });
  return response.data;
};

export const fetchTicket = async (connector, ticketId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/ticket/${ticketId}`);
  return response.data;
};

export const fetchMyTickets = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/ticket/my');
  return response.data;
};

export const createTicketBySelf = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/ticket/self', body);
  return response.data;
};

export const createTicketByAdmin = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/ticket', body);
  return response.data;
};

export const updateTicket = async (connector, ticketId, body) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/ticket/${ticketId}`, body);
  return response.data;
};

export const assignTicket = async (connector, ticketId, assigneeId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/ticket/${ticketId}/assign`, { assigneeId });
  return response.data;
};

export const changeTicketStatus = async (connector, ticketId, status) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/ticket/${ticketId}/status`, { status });
  return response.data;
};

export const completeTicket = async (connector, ticketId, body) => {
  // body가 undefined면(PURCHASE가 아닌 완료 처리) axios가 Content-Type 헤더 자체를 보내지 않아
  // 서버의 consumes=application/json 매칭이 깨진다(HttpMediaTypeNotSupportedException, 응답은 404로 변환됨) -
  // 항상 유효한 JSON 바디를 보내야 한다.
  const response = await connector.client.patch(
    `/api/v1/smart-asset/ticket/${ticketId}/complete`,
    body ? withApiDateTimes(body, ['acquisitionDate']) : {},
  );
  return response.data;
};

export const fetchTicketComments = async (connector, ticketId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/ticket/${ticketId}/comment`);
  return response.data;
};

export const addTicketComment = async (connector, ticketId, content) => {
  const response = await connector.client.post(`/api/v1/smart-asset/ticket/${ticketId}/comment`, { content });
  return response.data;
};
