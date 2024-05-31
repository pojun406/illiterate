import { useRef, useEffect, useCallback } from 'react';

const useScrollFadeInLeft = (duration: number = 1, delay: number = 0) => {
  const dom = useRef<HTMLDivElement | null>(null);

  const handleDirection = (): string => {
    return 'translate3d(50%, 0, 0)'; // 오른쪽에서 왼쪽으로 이동
  };

  const handleScroll = useCallback(
    (entries: IntersectionObserverEntry[]) => {
      const { current } = dom;
      if (current) {
        if (entries[0].isIntersecting) {
          current.style.transitionProperty = 'all';
          current.style.transitionDuration = `${duration}s`;
          current.style.transitionTimingFunction = 'cubic-bezier(0, 0, 0.2, 1)';
          current.style.transitionDelay = `${delay}s`;
          current.style.opacity = '1';
          current.style.transform = 'translate3d(0, 0, 0)';
        } else {
          current.style.transitionProperty = 'all';
          current.style.transitionDuration = `${duration}s`;
          current.style.transitionTimingFunction = 'cubic-bezier(0, 0, 0.2, 1)';
          current.style.transitionDelay = `${delay}s`;
          current.style.opacity = '0';
          current.style.transform = handleDirection();
        }
      }
    },
    [delay, duration],
  );

  useEffect(() => {
    const { current } = dom;

    if (current) {
      const observer = new IntersectionObserver((entries) => handleScroll(entries), { threshold: 0.7 });
      observer.observe(current);

      return () => observer.disconnect();
    }
  }, [handleScroll]);

  return {
    ref: dom,
    style: {
      opacity: 0,
      transform: handleDirection(),
    },
  };
};

export default useScrollFadeInLeft;
