import { useState, useCallback } from 'react';
import { winiMsg } from '@/shared/model';
import { getNoticeDetail, deleteNotice } from '../api/api';
import { winiCom } from '@/shared/lib';

export const useNoticeDetail = (params) => {
    const { onBackToList, onOpenWriteWithData } = params;
    const { connector } = winiCom.getFormInfo('Y');

    const [notice, setNotice] = useState(null);

    const setDetail = useCallback((detail) => setNotice(detail), []);

    const fetchDetail = useCallback(async (noticeId) => {
        if (!connector || !noticeId) return null;
        try {
            const data = await getNoticeDetail(connector, noticeId);
            if (data?.result === 'SUCCESS') {
                setNotice(data.data);
            }
            return data;
        } catch (e) {
            winiMsg.showSnackbar('공지사항 상세 조회 중 오류 발생');
            return null;
        }
    }, [connector]);

    const back = useCallback(() => onBackToList?.(), [onBackToList]);

    const edit = useCallback(() => {
        if (!notice) return;
        onOpenWriteWithData?.(notice);
    }, [notice, onOpenWriteWithData]);

    const remove = useCallback(async () => {
        if (!notice?.noticeId) return;

        const result = await winiMsg.showConfirm('삭제하시겠습니까?');
        if (result !== 'Y') return;

        try {
            const data = await deleteNotice(connector, notice.noticeId);
            if (data?.result === 'SUCCESS') {
                winiMsg.showSnackbar('공지사항이 성공적으로 삭제되었습니다.');
                onBackToList?.();
            }
        } catch (e) {
            winiMsg.showSnackbar('공지사항 삭제 중 오류 발생');
        }
    }, [connector, notice, onBackToList]);

    return {
        notice,
        setDetail,
        fetchDetail,
        back,
        edit,
        remove,
    };
};
