import { useCallback, useRef } from 'react';
import { createSoftware, updateSoftware, deleteSoftware } from '@/entities/software';
import { winiCom } from '@/shared/lib';

export const useSoftwareActions = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const create = useCallback(async (body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return createSoftware(currentConnector, body);
  }, []);

  const update = useCallback(async (softwareId, body) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return updateSoftware(currentConnector, softwareId, body);
  }, []);

  const remove = useCallback(async (softwareId) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    return deleteSoftware(currentConnector, softwareId);
  }, []);

  return { create, update, remove };
};
