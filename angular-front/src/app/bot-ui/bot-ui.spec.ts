import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BotUi } from './bot-ui';

describe('BotUi', () => {
  let component: BotUi;
  let fixture: ComponentFixture<BotUi>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BotUi],
    }).compileComponents();

    fixture = TestBed.createComponent(BotUi);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
