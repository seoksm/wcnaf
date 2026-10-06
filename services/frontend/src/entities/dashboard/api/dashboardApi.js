/**
 * 대시보드(dashboard) 엔티티 API - S-700 위젯 데이터, S-701 위젯 설정
 */

export const fetchDashboardSummary = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/dashboard/summary');
  return response.data;
};

export const fetchDashboardWidgetConfig = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/dashboard/widget-config');
  return response.data;
};

/**
 * PUT이 아니라 PATCH를 쓰는 이유 - API 게이트웨이의 AuthorizationFilter가
 * GET/POST/PATCH/DELETE만 authType(SELECT/INSERT/UPDATE/DELETE)으로 매핑하고 PUT은
 * 인식하지 못해 등록된 권한이 있어도 항상 미인가(404로 감싸진 403)로 거부한다.
 */
export const saveDashboardWidgetConfig = async (connector, configs) => {
  const response = await connector.client.patch('/api/v1/smart-asset/dashboard/widget-config', { configs });
  return response.data;
};
