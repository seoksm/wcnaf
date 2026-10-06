import { useEffect, useRef, forwardRef, Component, createRef } from 'react';
import Viewer from '@toast-ui/editor/dist/toastui-editor-viewer';

import '@toast-ui/editor/toastui-editor.css';
import { customHtmlImageRenderer } from '@/shared/ui/wini';
import 'tui-color-picker/dist/tui-color-picker.css';
import '@toast-ui/editor-plugin-color-syntax/dist/toastui-editor-plugin-color-syntax.css';
import '@toast-ui/editor-plugin-table-merged-cell/dist/toastui-editor-plugin-table-merged-cell.css';
import tableMergedCell from '@toast-ui/editor-plugin-table-merged-cell';
import Prism from 'prismjs';
// import 'prismjs/themes/prism.css';
import "prismjs/themes/prism-tomorrow.css"; // 다크 테마
import '@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight.css';
import codeSyntaxHighlight from '@toast-ui/editor-plugin-code-syntax-highlight';

const ViewerComponent = forwardRef((props, ref) => {
  const rootEl = useRef(null);
  const viewerInst = useRef(null);

  const getBindingEventNames = () => {
    return Object.keys(props)
      .filter((key) => /^on[A-Z][a-zA-Z]+/.test(key))
      .filter((key) => props[key]);
  };

  const bindEventHandlers = (props) => {
    getBindingEventNames().forEach((key) => {
      const eventName = key[2].toLowerCase() + key.slice(3);
      viewerInst.current.off(eventName);
      viewerInst.current.on(eventName, props[key]);
    });
  };

  const getInitEvents = () => {
    return getBindingEventNames().reduce((acc, key) => {
      const eventName = key[2].toLowerCase() + key.slice(3);
      acc[eventName] = props[key];
      return acc;
    }, {});
  };

  useEffect(() => {
    let plulginList = [
      [codeSyntaxHighlight, { highlighter: Prism }],
      tableMergedCell,
    ];
    if (props.plugin !== undefined && props.plugin !== null) {
      plulginList = [...plulginList, ...props.plugin];
    }
    viewerInst.current = new Viewer({
      el: rootEl.current,
      ...props,
      plugins: plulginList,
      events: getInitEvents(),
      linkAttributes: {
        target: '_blank',
        rel: 'noopener noreferrer',
      },
      customHTMLRenderer: {
        image(node, context) {
          return customHtmlImageRenderer(node, context);
        },
      },
    });

    if (ref) {
      ref.current = {
        viewerInst: viewerInst,
      };
    }

    return () => {
      viewerInst.current.destroy();
    };
  }, []);

  useEffect(() => {
    viewerInst.current.setMarkdown(props.value || '');
  }, [props.value]);

  useEffect(() => {
    bindEventHandlers(props);
  }, [props]);

  return <div ref={rootEl} />;
});

export default ViewerComponent;