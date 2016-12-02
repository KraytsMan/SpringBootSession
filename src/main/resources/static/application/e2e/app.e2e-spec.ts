import { SkeletonAngular2Page } from './app.po';

describe('skeleton-angular-2 App', function() {
  let page: SkeletonAngular2Page;

  beforeEach(() => {
    page = new SkeletonAngular2Page();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
